package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.sql.Connection

internal class DatabaseSetMockTest {
    private val connection: Connection = mock()
    private val database = spy(Database(connection))
    private val set = database.set(0)

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countMap(set.layer)

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)
        doReturn(0).whenever(database).countMap(set.layer)

        assert.that(set == database.set(set.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(set == database.set(set.layer + 1), equalTo(false))
    }

    @Test
    fun shouldEqualsReflexively() {
        assert.that(set == set, equalTo(true))
    }

    @Test
    fun shouldNotEqualsTrivially() {
        assert.that(set as DatabaseSet? == null, equalTo(false))
    }

    @Test
    fun shouldNotEqualsXenoxively() {
        assert.that(set as Any == this, equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(set.hashCode() == database.set(set.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(1)

        assert.that(
                set.hashCode() == database.set(set.layer + 1).hashCode(),
                equalTo(false))
    }

    @Test
    fun shouldFindEntry() {
        doReturn(iteratorOf("foo")).whenever(database).
                selectLayerKeys(0)

        assert.that(set.contains(database.databaseEntry(set.layer, "foo")),
                equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doReturn(mutableIteratorOf("foo")).whenever(database).
                selectLayerKeys(0)
        doNothing().whenever(database).deleteOne(set.layer, "foo")
        doReturn("3").whenever(database).selectOne(set.layer, "foo")

        assert.that(set.remove(database.databaseEntry(set.layer, "foo")),
                equalTo(true))

        verify(database).deleteOne(set.layer, "foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().whenever(database).deleteOne(set.layer, "foo")
        doReturn(null, "3").whenever(database).selectOne(set.layer, "foo")

        val changed = set.add(database.databaseEntry(set.layer, "foo"))

        assert.that(changed, equalTo(true))
    }
}
