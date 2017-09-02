package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.test.fail

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseTest {
    @Mock private lateinit var connection: Connection
    @Mock private lateinit var statement: PreparedStatement
    @Mock private lateinit var results: ResultSet
    @InjectMocks private lateinit var database: Database

    @Before
    fun setUp() {
        `when`(connection.prepareStatement(anyString())).thenReturn(statement)
        `when`(statement.executeQuery()).thenReturn(results)
    }

    @Test
    fun shouldCountAll() {
        `when`(results.next()).thenReturn(true, false)
        `when`(results.getInt(eq("size"))).thenReturn(3)

        assert.that(database.countAll(0), equalTo(3))

        val inOrder = inOrder(statement, results)
        inOrder.verify(statement).setInt(1, 0)
        inOrder.verify(statement).executeQuery()
        inOrder.verify(results).close()
        verify(statement, never()).close()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenCountAllHasNone() {
        `when`(results.next()).thenReturn(false)

        database.countAll(0)
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenCountAllHasMultiple() {
        `when`(results.next()).thenReturn(true, true, false)
        `when`(results.getInt(eq("size"))).thenReturn(3)

        database.countAll(0)
    }

    @Test
    fun shouldSelectOne() {
        `when`(results.next()).thenReturn(true, false)
        `when`(results.getString(eq("value"))).thenReturn("3")

        val value = database.selectOne(0, "foo")

        assert.that(value, equalTo("3"))

        // TODO: How to verify setting params are order free?
        val inOrder = inOrder(statement, results)
        inOrder.verify(statement).setInt(1, 0)
        inOrder.verify(statement).setString(2, "foo")
        inOrder.verify(statement).executeQuery()
        inOrder.verify(results).close()
        verify(statement, never()).close()
    }

    @Test
    fun shouldReturnNullWhenSelectOneHasNone() {
        `when`(results.next()).thenReturn(false)

        val value = database.selectOne(0, "foo")

        assert.that(value, absent())
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenSelectOneHasMultiple() {
        `when`(results.next()).thenReturn(true, true, false)
        `when`(results.getString(eq("value"))).thenReturn("3")

        database.selectOne(0, "foo")
    }

    @Test
    fun shouldUpsertOne() {
        database.upsertOne(0, "foo", "3")

        // TODO: How to verify setting params are order free?
        val inOrder = inOrder(statement)
        inOrder.verify(statement).setInt(1, 0)
        inOrder.verify(statement).setString(2, "foo")
        inOrder.verify(statement).setString(3, "3")
        inOrder.verify(statement).executeUpdate()
        verify(statement, never()).close()
    }

    @Test
    fun shouldDeleteOne() {
        database.deleteOne(0, "foo")

        // TODO: How to verify setting params are order free?
        val inOrder = inOrder(statement)
        inOrder.verify(statement).setInt(1, 0)
        inOrder.verify(statement).setString(2, "foo")
        inOrder.verify(statement).executeUpdate()
        verify(statement, never()).close()
    }

    @Test
    fun shouldCommit() {
        database.transaction { }

        val inOrder = inOrder(connection)
        inOrder.verify(connection, times(1)).autoCommit = false
        inOrder.verify(connection, times(1)).commit()
        inOrder.verify(connection, times(1)).autoCommit = true
        verify(connection, never()).rollback()
    }

    @Suppress("UNREACHABLE_CODE")
    @Test
    fun shouldRollback() = try {
        database.transaction { throw SQLException() }
        fail("Did not throw")
    } catch (e: SQLException) {
        val inOrder = inOrder(connection)
        inOrder.verify(connection, times(1)).autoCommit = false
        inOrder.verify(connection, times(1)).rollback()
        inOrder.verify(connection, times(1)).autoCommit = true
        verify(connection, never()).commit()
    }

    @Test
    fun shouldClose() {
        connection.close()

        verify(connection, times(1)).close()
    }
}
