package hm.binkley.knapsack

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseEntryIteratorTest {
    @Mock private lateinit var database: Database
    @Mock private lateinit var selectAllResults: ResultSet
    private lateinit var iter: DatabaseEntryIterator

    @Before
    fun setUpDatabase() {
        doReturn(selectAllResults).`when`(database).selectAll()

        iter = DatabaseEntryIterator(database)
    }

    @Test
    fun shouldClose() {
        iter.use {}

        verify(selectAllResults, atLeastOnce()).close()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextBeforeStart() {
        `when`(selectAllResults.isBeforeFirst).thenReturn(true)

        iter.next()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextAfterEnd() {
        `when`(selectAllResults.isAfterLast).thenReturn(true)

        iter.next()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveBeforeStart() {
        `when`(selectAllResults.isBeforeFirst).thenReturn(true)

        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveAfterEnd() {
        `when`(selectAllResults.isAfterLast).thenReturn(true)

        iter.remove()
    }
}
