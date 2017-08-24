package hm.binkley.knapsack

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseEntryIteratorTest {
    @Mock private lateinit var allResults: ResultSet
    @Mock private lateinit var selectOne: PreparedStatement
    @Mock private lateinit var upsertOne: PreparedStatement
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var iter: DatabaseEntryIterator

    @Before
    fun setUpDatabase() {
        iter = DatabaseEntryIterator(allResults, selectOne, upsertOne,
                deleteOne)
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
