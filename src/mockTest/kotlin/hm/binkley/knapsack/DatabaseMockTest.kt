package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.test.fail

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseMockTest {
    private val results: ResultSet = mock()
    private val statement: PreparedStatement = mock {
        on { executeQuery() } doReturn results
    }
    private val connection: Connection = mock {
        on { prepareStatement(any()) } doReturn statement
    }
    private val database = Database(connection)

    @Test
    fun shouldCountMap() {
        whenever(results.next()).thenReturn(true, false)
        whenever(results.getInt(eq("size"))).thenReturn(3)

        assert.that(database.countMap(0), equalTo(3))

        val inOrder = inOrder(statement, results)
        inOrder.verify(statement).setInt(1, 0)
        inOrder.verify(statement).executeQuery()
        inOrder.verify(results).close()
        verify(statement, never()).close()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenCountMapHasNone() {
        whenever(results.next()).thenReturn(false)

        database.countMap(0)
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenCountMapHasMultiple() {
        whenever(results.next()).thenReturn(true, true, false)
        whenever(results.getInt(eq("size"))).thenReturn(3)

        database.countMap(0)
    }

    @Test
    fun shouldSelectOne() {
        whenever(results.next()).thenReturn(true, false)
        whenever(results.getString(eq("value"))).thenReturn("3")

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
        whenever(results.next()).thenReturn(false)

        val value = database.selectOne(0, "foo")

        assert.that(value, absent())
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenSelectOneHasMultiple() {
        whenever(results.next()).thenReturn(true, true, false)
        whenever(results.getString(eq("value"))).thenReturn("3")

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
