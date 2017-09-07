package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert

import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

internal class DatabaseMapListLiveTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldWorkEndToEnd() {
        val mapList = KNAPSACK.database.mapList()

        assert.that(mapList.isEmpty(), equalTo(true))
        assert.that(mapList.keys.isEmpty(), equalTo(true))
        assert.that(mapList.values.isEmpty(), equalTo(true))
        assert.that(mapList.entries.isEmpty(), equalTo(true))
        mapList["foo"] = "3"
        assert.that(mapList.size, equalTo(1))
        assert.that(mapList.keys.size, equalTo(1))
        assert.that(mapList.values.size, equalTo(1))
        assert.that(mapList.entries.size, equalTo(1))
        assert.that(mapList["foo"], equalTo("3"))
        assert.that(mapList.containsKey("foo"), equalTo(true))
        assert.that(mapList.containsKey("bar"), equalTo(false))
        assert.that(mapList.containsValue("3"), equalTo(true))
        assert.that(mapList.containsValue("4"), equalTo(false))

        assert.that(mapList.remove("foo", "3"), equalTo(true))
        assert.that(mapList.remove("foo", "3"), equalTo(false))
        assert.that(mapList.isEmpty(), equalTo(true))
        mapList["foo"] = "3"
        assert.that(mapList.remove("foo"), equalTo("3"))
        assert.that(mapList.isEmpty(), equalTo(true))
        assert.that(mapList.getOrDefault("foo", "3"),
                equalTo("3"))

        mapList["foo"] = "3"
        assert.that(mapList, equalTo(KNAPSACK.database.mapList()))
        assert.that(mapList.hashCode(),
                equalTo(KNAPSACK.database.mapList().hashCode()))

        mapList["bar"] = "4"
        assert.that(mapList.keys, equalTo(setOf("foo", "bar")))

        mapList.next()
        assert.that(mapList.keys.isEmpty(), equalTo(true))
        assert.that(mapList == KNAPSACK.database.mapList(), equalTo(false))
        assert.that(
                mapList.hashCode() == KNAPSACK.database.mapList().hashCode(),
                equalTo(false))
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = DatabaseLiveTestRule()
    }
}
