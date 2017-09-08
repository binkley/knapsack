package hm.binkley.knapsack

import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseEntryIteratorMockTest {
    @Mock private lateinit var database: Database
    @Mock private lateinit var selectKeysResults: ResultSet
    private lateinit var iter: DatabaseEntryIterator

    @Before
    fun setUpDatabase() {
        val layer = 0
        doReturn(selectKeysResults).whenever(database).selectMapKeys(layer)
        iter = database.entryIterator(layer)
    }

    @Test
    fun shouldClose() {
        iter.use {}

        verify(selectKeysResults, atLeastOnce()).close()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextBeforeStart() {
        whenever(selectKeysResults.isBeforeFirst).thenReturn(true)

        iter.next()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextAfterEnd() {
        whenever(selectKeysResults.isAfterLast).thenReturn(true)

        iter.next()
    }

    @Test
    fun shouldRemove() {
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")
        whenever(selectKeysResults.row).thenReturn(1)

        iter.remove()
    }

    @Test
    fun shouldRemoveTwiceIfNextBetween() {
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")
        whenever(selectKeysResults.row).thenReturn(1, 2)

        iter.remove()
        iter.next()
        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveBeforeStart() {
        whenever(selectKeysResults.isBeforeFirst).thenReturn(true)

        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveAfterEnd() {
        whenever(selectKeysResults.isAfterLast).thenReturn(true)

        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveTwiceOnSameRow() {
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")
        whenever(selectKeysResults.row).thenReturn(1, 1)

        iter.remove()
        iter.remove()
    }
}
