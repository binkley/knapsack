package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.sql.Connection

internal class DatabaseListMockTest {
    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val list = database.list()

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countList()

        assert.that(list.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        doReturn(0).whenever(database).countList()

        assert.that(list.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldSizeWhenMapped() {
        doReturn(1).whenever(database).countList()

        assert.that(list.size, equalTo(1))
    }

    @Test
    fun shouldIterateWhenMapped() {
        doReturn(1).whenever(database).countList()

        val iter = list.iterator()
        assert.that(iter.hasNext(), equalTo(true))
        iter.next()
        assert.that(iter.hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0).whenever(database).countList()

        assert.that(list == database.list(), equalTo(true))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        doReturn(0).whenever(database).countList()

        assert.that(list.hashCode() == database.list().hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldContains() {
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)
        doReturn(1).whenever(database).countList()
        doReturn(1).whenever(database).countMap(0)

        assert.that(list.contains(database.map(0)), equalTo(true))
    }

    @Test
    fun shouldIndexOf() {
        doReturn(1).whenever(database).countList()
        doReturn(0, 0).whenever(database).countMap(0)
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(list.indexOf(database.map(0)), equalTo(0))
    }

    @Test
    fun shouldLastIndexOf() {
        doReturn(1).whenever(database).countList()
        doReturn(0, 0).whenever(database).countMap(0)
        doReturn(iteratorOf()).whenever(database).selectLayerKeys(0)

        assert.that(list.lastIndexOf(database.map(0)), equalTo(0))
    }
}
