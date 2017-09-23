package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

internal class DatabaseEntryIteratorMockTest {
    private val keys: MutableIterator<String> = mock()
    private val database: Database = mock {
        on { selectLayerKeys(0) } doReturn keys
    }
    private val iter = database.entryIterator(0)

    @Test
    fun shouldLayer_forJaCoCo() {
        assert.that(iter.layer, equalTo(0))
    }

    @Test
    fun shouldDelegateHasNext() {
        iter.hasNext()

        verify(keys).hasNext()
    }

    @Test
    fun shouldDelegateNext() {
        whenever(keys.next()).thenReturn("foo")

        iter.hasNext()
        iter.next()

        verify(keys).next()
    }

    @Test
    fun shouldDelegateRemove() {
        whenever(keys.next()).thenReturn("foo")

        iter.hasNext()
        iter.next()
        iter.remove()

        verify(keys).remove()
    }
}
