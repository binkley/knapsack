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
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseEntryIteratorTest {
    @Mock private lateinit var loader: SQLLoader
    @Mock private lateinit var selectAll: PreparedStatement
    @Mock private lateinit var allResults: ResultSet
    private lateinit var iter: DatabaseEntryIterator

    @Before
    fun setUpDatabase() {
        doReturn(selectAll).`when`(loader).prepareSelectAll

        `when`(selectAll.executeQuery()).thenReturn(allResults)

        iter = DatabaseEntryIterator(loader)
    }

    @Test
    fun shouldClose() {
        iter.use {}

        verify(allResults, atLeastOnce()).close()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldCarpIfBeforeStart() {
        `when`(allResults.isBeforeFirst).thenReturn(true)

        iter.next()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldCarpIfAfterEnd() {
        `when`(allResults.isAfterLast).thenReturn(true)

        iter.next()
    }
}
