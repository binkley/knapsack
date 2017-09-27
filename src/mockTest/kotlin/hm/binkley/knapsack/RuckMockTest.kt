package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import java.sql.Connection

internal class RuckMockTest {
    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val ruck = Ruck(database)

    @Test
    fun shouldGetAndCache() {
        doReturn(3).whenever(database).countList()
        layerOf(0, "foo" to "3")
        layerOf(1)
        layerOf(2, "foo" to "4")

        ruck["foo"] = { key, layers ->
            layers[key].filterNotNull().map { it.toInt() }.sum()
        }

        assert.that(ruck["foo"], equalTo(7))
        assert.that(ruck["foo"], equalTo(7))

        verify(database, times(1)).selectOne(0, "foo")
        verify(database, times(1)).selectOne(2, "foo")
    }

    private fun layerOf(layer: Int, vararg pairs: Pair<String, String>) {
        doReturn(pairs.size).whenever(database).countMap(layer)
        doReturn(pairs.map { it.first }.iterator()).whenever(database).
                selectLayerKeys(layer)
        pairs.forEach { (key, value) ->
            doReturn(value).whenever(database).selectOne(layer, key)
        }
    }
}
