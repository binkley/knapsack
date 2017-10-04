package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import hm.binkley.knapsack.Value.DatabaseValue
import hm.binkley.knapsack.Value.NoValue
import org.junit.Test

internal class DatabaseMapMockTest {
    private val database: Database = mock {
        on { countList() } doReturn 2
    }
    private val map = database.map(0)

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countMap(map.layer)

        assert.that(map.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(map.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)
        doReturn(0).whenever(database).countMap(map.layer)

        assert.that(map == database.map(map.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(map == database.map(map.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(map.hashCode() == database.map(map.layer).hashCode(),
                equalTo(true))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun shouldThrowOnNegativeLayer() {
        database.map(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun shouldThrowOnExcessiveLayer() {
        database.map(3)
    }

    @Test
    fun shouldContainKey() {
        fooIsThree()

        assert.that(map.containsKey("foo"), equalTo(true))
    }

    @Test
    fun shouldNotContainKey() {
        fooIsThree()

        assert.that(map.containsKey("bar"), equalTo(false))
    }

    @Test
    fun shouldNotContainKeyWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(map.containsKey("foo"), equalTo(false))
    }

    @Test
    fun shouldContainValue() {
        fooIsThree()

        assert.that(map.containsValue("3"), equalTo(true))
    }

    @Test
    fun shouldNotContainValue() {
        fooIsThree()

        assert.that(map.containsValue("4"), equalTo(false))
    }

    @Test
    fun shouldNotContainValueWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(map.containsValue("3"), equalTo(false))
    }

    @Test
    fun shouldGet() {
        fooIsThree()

        assert.that((map["foo"] as DatabaseValue).value, equalTo("3"))
    }

    @Test
    fun shouldGetOrDefault() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that((map.getOrDefault("bar", "4") as DatabaseValue).value,
                equalTo("4"))
    }

    @Test
    fun shouldRemove() {
        fooIsThree()
        doNothing().whenever(database).deleteOne(map.layer, "foo")

        assert.that((map.remove("foo") as DatabaseValue).value, equalTo("3"))
        verify(database).deleteOne(map.layer, "foo")
    }

    @Test
    fun shouldNotRemove() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        val oldValue = map.remove("bar")

        assert.that(oldValue == NoValue, equalTo(true))
        verify(database, never()).deleteOne(map.layer, "bar")
    }

    @Test
    fun shouldRemoveWithValue() {
        fooIsThree()
        doNothing().whenever(database).deleteOne(map.layer, "foo")

        val removed = map.remove("foo", "3")

        assert.that(removed, equalTo(true))
        verify(database).deleteOne(map.layer, "foo")
    }

    @Test
    fun shouldNotRemoveWithoutValue() {
        fooIsThree()

        val removed = map.remove("foo", "4")

        assert.that(removed, equalTo(false))
        verify(database, never()).deleteOne(map.layer, "foo")
    }

    private fun fooIsThree() {
        doReturn(iteratorOf("foo")).whenever(database).selectLayerKeys(0)
        doReturn("3").whenever(database).selectOne(map.layer, "foo")
        map.put("foo", database.value(map.layer, "foo", "3"))
    }
}
