package hm.binkley.knapsack

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
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
    @Mock private lateinit var selectKeysResults: ResultSet
    private lateinit var iter: DatabaseEntryIterator

    @Before
    fun setUpDatabase() {
        val layer = 0
        doReturn(selectKeysResults).`when`(database).selectMapKeys(layer)
        iter = database.entryIterator(layer)
    }

    @Test
    fun shouldClose() {
        iter.use {}

        verify(selectKeysResults, atLeastOnce()).close()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextBeforeStart() {
        `when`(selectKeysResults.isBeforeFirst).thenReturn(true)

        iter.next()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextAfterEnd() {
        `when`(selectKeysResults.isAfterLast).thenReturn(true)

        iter.next()
    }

    @Test
    fun shouldRemove() {
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")
        `when`(selectKeysResults.row).thenReturn(1)

        iter.remove()
    }

    @Test
    fun shouldRemoveTwiceIfNextBetween() {
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")
        `when`(selectKeysResults.row).thenReturn(1, 2)

        iter.remove()
        iter.next()
        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveBeforeStart() {
        `when`(selectKeysResults.isBeforeFirst).thenReturn(true)

        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveAfterEnd() {
        `when`(selectKeysResults.isAfterLast).thenReturn(true)

        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveTwiceOnSameRow() {
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")
        `when`(selectKeysResults.row).thenReturn(1, 1)

        iter.remove()
        iter.remove()
    }
}
