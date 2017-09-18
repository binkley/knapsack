package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.ResultSet

internal class DatabaseSetMockTest {
    private val connection: Connection = mock()
    private val database = spy(Database(connection))
    private val selectKeysResults: ResultSet = mock()
    private val otherSelectKeysResults: ResultSet = mock()
    private val set = database.set(0)

    @Before
    fun setUpDatabase() {
        doReturn(selectKeysResults).whenever(database).selectMapKeys(0)
        doReturn(otherSelectKeysResults).whenever(database).selectMapKeys(1)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countMap(set.layer)

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        whenever(selectKeysResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).whenever(database).countMap(set.layer)

        assert.that(set == database.set(set.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(set == database.set(set.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false, false)

        assert.that(set.hashCode() == database.set(set.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false)
        whenever(otherSelectKeysResults.next()).thenReturn(false)

        assert.that(
                set.hashCode() == database.set(set.layer + 1).hashCode(),
                equalTo(false))
    }

    @Test
    fun shouldFindEntry() {
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(database.entry(set.layer, "foo")),
                equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doNothing().whenever(database).deleteOne(set.layer, "foo")
        doReturn("3").whenever(database).selectOne(set.layer, "foo")
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.row).thenReturn(1)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(database.entry(set.layer, "foo")),
                equalTo(true))

        verify(database).deleteOne(set.layer, "foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().whenever(database).deleteOne(set.layer, "foo")
        doReturn(null, "3").whenever(database).selectOne(set.layer, "foo")

        val changed = set.add(database.entry(set.layer, "foo"))

        assert.that(changed, equalTo(true))
    }
}
