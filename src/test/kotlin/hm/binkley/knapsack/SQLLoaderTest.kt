package hm.binkley.knapsack

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.sql.SQLException
import kotlin.test.fail

@RunWith(MockitoJUnitRunner::class)
internal class SQLLoaderTest {
    @Mock private lateinit var database: Database
    @InjectMocks private lateinit var loader: SQLLoader

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
