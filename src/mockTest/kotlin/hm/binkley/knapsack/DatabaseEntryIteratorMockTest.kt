package hm.binkley.knapsack

import com.nhaarman.mockito_kotlin.atLeastOnce
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.sql.ResultSet
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class DatabaseEntryIteratorMockTest {
    private val selectKeysResults: ResultSet = mock()
    private val database: Database = mock {
        on { selectMapKeys(0) } doReturn selectKeysResults
    }
    private val iter = database.entryIterator(0)

    @Test
    fun shouldClose() {
        iter.use {}

        verify(selectKeysResults, atLeastOnce()).close()
    }

    @Test
    fun shouldHasNext() {
        whenever(selectKeysResults.next()).thenReturn(true, false)

        assertTrue(iter.hasNext())
        assertFalse(iter.hasNext())
    }

    @Test
    fun shouldNext() {
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        iter.hasNext()
        assertEquals(iter.next().key, "foo")
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextBeforeStart() {
        iter.next()
    }

    @Test(expected = NoSuchElementException::class)
    fun shouldThrowIfNextAfterEnd() {
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        iter.hasNext()
        iter.hasNext()
        iter.next()
    }

    @Test
    fun shouldRemove() {
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        iter.hasNext()
        iter.next()
        iter.remove()
    }

    @Test
    fun shouldRemoveTwiceIfNextBetween() {
        whenever(selectKeysResults.next()).thenReturn(true, true, false)
        whenever(selectKeysResults.getString(eq("key"))).
                thenReturn("foo", "bar")

        iter.hasNext()
        iter.next()
        iter.remove()
        iter.hasNext()
        iter.next()
        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveWithoutNext() {
        iter.hasNext()
        iter.remove()
    }

    @Test(expected = IllegalStateException::class)
    fun shouldThrowIfRemoveTwiceOnSameRow() {
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        iter.hasNext()
        iter.next()
        iter.remove()
        iter.remove()
    }
}
