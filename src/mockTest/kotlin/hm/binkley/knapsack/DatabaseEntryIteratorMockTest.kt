package hm.binkley.knapsack

import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.sql.ResultSet
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DatabaseEntryIteratorMockTest {
    private val selectKeysResults: ResultSet = mock()
    private val database: Database = mock {
        on { selectMapKeys(0) } doReturn selectKeysResults
    }

    @Test
    fun shouldClose() {
        iter.use {}

        verify(selectKeysResults, atLeastOnce()).close()
    }

    private val iter = database.entryIterator(0)

    @Test
    fun shouldHasNext() {
        whenever(selectKeysResults.next()).thenReturn(true, false)

        assertTrue(iter.hasNext())
        assertFalse(iter.hasNext())
    }

    @Test
    fun shouldNext() {
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        iter.next()
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
