package hm.binkley.knapsack

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseEntryIteratorTest {
    @Mock private lateinit var loader: SQLLoader
    @Mock private lateinit var selectAll: PreparedStatement
    @Mock private lateinit var allResults: ResultSet
    @Mock private lateinit var selectOne: PreparedStatement
    @Mock private lateinit var upsertOne: PreparedStatement
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var iter: DatabaseEntryIterator

    @Before
    fun setUpDatabase() {
        `when`(selectAll.executeQuery()).thenReturn(allResults)

        iter = DatabaseEntryIterator(loader, selectAll, selectOne, upsertOne,
                deleteOne)
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
