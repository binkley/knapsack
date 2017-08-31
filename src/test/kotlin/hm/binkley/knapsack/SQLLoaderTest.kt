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
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.test.fail

@RunWith(MockitoJUnitRunner::class)
internal class SQLLoaderTest {
    @Mock private lateinit var database: Database
    @Mock private lateinit var statement: PreparedStatement
    @Mock private lateinit var results: ResultSet
    @InjectMocks private lateinit var loader: SQLLoader

    @Before
    fun setUp() {
        `when`(database.prepareStatement(anyString())).thenReturn(statement)
        `when`(statement.executeQuery()).thenReturn(results)
    }

    @Test
    fun shouldSelectOne() {
        `when`(results.next()).thenReturn(true, false)
        `when`(results.getString(eq("value"))).thenReturn("3")

        val value = loader.selectOne("foo")

        assert.that(value, equalTo("3"))

        val inOrder = inOrder(statement, results)
        inOrder.verify(statement).setString(1, "foo")
        inOrder.verify(statement).executeQuery()
        inOrder.verify(results).close()
        verify(statement, never()).close()
    }

    @Test
    fun shouldReturnNullWhenSelectOneHasNone() {
        `when`(results.next()).thenReturn(false)

        val value = loader.selectOne("foo")

        assert.that(value, absent())
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowWhenSelectOneHasMultiple() {
        `when`(results.next()).thenReturn(true, true, false)
        `when`(results.getString(eq("value"))).thenReturn("3")

        loader.selectOne("foo")
    }

    @Test
    fun shouldCommit() {
        loader.transaction { }

        val inOrder = inOrder(database)
        inOrder.verify(database, times(1)).autoCommit = false
        inOrder.verify(database, times(1)).commit()
        inOrder.verify(database, times(1)).autoCommit = true
        verify(database, never()).rollback()
    }

    @Suppress("UNREACHABLE_CODE")
    @Test
    fun shouldRollback() = try {
        loader.transaction { throw SQLException() }
        fail("Did not throw")
    } catch (e: SQLException) {
        val inOrder = inOrder(database)
        inOrder.verify(database, times(1)).autoCommit = false
        inOrder.verify(database, times(1)).rollback()
        inOrder.verify(database, times(1)).autoCommit = true
        verify(database, never()).commit()
    }

    @Test
    fun shouldClose() {
        loader.close()

        verify(database, times(1)).close()
    }
}
