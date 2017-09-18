package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doReturn
import java.sql.Connection
import java.sql.ResultSet

internal class DatabaseMapMockTest {
    private val connection: Connection = mock()
    private val database = spy(Database(connection))
    private val selectKeysResults: ResultSet = mock()
    private val otherSelectKeysResults: ResultSet = mock()
    private lateinit var map: DatabaseMap

    @Before
    fun setUp() {
        doReturn(2).whenever(database).countList()
        map = database.map(0)
        doReturn(selectKeysResults).whenever(database).selectMapKeys(
                map.layer)
        doReturn(otherSelectKeysResults).whenever(database).selectMapKeys(
                map.layer + 1)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countMap(map.layer)

        assert.that(map.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        whenever(selectKeysResults.next()).thenReturn(false)

        assert.that(map.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).whenever(database).countMap(map.layer)

        assert.that(map == database.map(map.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(map == database.map(map.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false, false)

        assert.that(map.hashCode() == database.map(map.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false)
        whenever(otherSelectKeysResults.next()).thenReturn(false)

        assert.that(
                map.hashCode() == database.map(map.layer + 1).hashCode(),
                equalTo(false))
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
        whenever(selectKeysResults.next()).thenReturn(false)

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
        whenever(selectKeysResults.next()).thenReturn(false)

        assert.that(map.containsValue("3"), equalTo(false))
    }

    @Test
    fun shouldGet() {
        fooIsThree()

        assert.that(map["foo"], equalTo("3"))
    }

    @Test
    fun shouldGetOrDefault() {
        assert.that(map.getOrDefault("bar", "4"), equalTo("4"))
    }

    @Test
    fun shouldRemove() {
        fooIsThree()
        doNothing().whenever(database).deleteOne(map.layer, "foo")

        val oldValue = map.remove("foo")

        assert.that(oldValue, equalTo("3"))
        verify(database).deleteOne(map.layer, "foo")
    }

    @Test
    fun shouldNotRemove() {
        val oldValue = map.remove("bar")

        assert.that(oldValue, absent())
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
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString("key")).thenReturn("foo")
        doReturn("3").whenever(database).selectOne(map.layer, "foo")
    }
}
