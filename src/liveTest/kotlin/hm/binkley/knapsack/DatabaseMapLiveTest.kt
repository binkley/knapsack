package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

internal class DatabaseMapLiveTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldWorkEndToEnd() {
        val map = KNAPSACK.database.map(0)

        assert.that(map.isEmpty(), equalTo(true))
        assert.that(map.keys.isEmpty(), equalTo(true))
        assert.that(map.values.isEmpty(), equalTo(true))
        assert.that(map.entries.isEmpty(), equalTo(true))
        map["foo"] = "3"
        assert.that(map.size, equalTo(1))
        assert.that(map.keys.size, equalTo(1))
        assert.that(map.values.size, equalTo(1))
        assert.that(map.entries.size, equalTo(1))
        assert.that(map["foo"], equalTo("3"))
        assert.that(map.containsKey("foo"), equalTo(true))
        assert.that(map.containsKey("bar"), equalTo(false))
        assert.that(map.containsValue("3"), equalTo(true))
        assert.that(map.containsValue("4"), equalTo(false))

        assert.that(map.remove("foo", "3"), equalTo(true))
        assert.that(map.remove("foo", "3"), equalTo(false))
        assert.that(map.isEmpty(), equalTo(true))
        map["foo"] = "3"
        assert.that(map.remove("foo"), equalTo("3"))
        assert.that(map.isEmpty(), equalTo(true))
        assert.that(map.getOrDefault("foo", "3"), equalTo("3"))

        map["foo"] = "3"
        assert.that(map, equalTo(KNAPSACK.database.map(map.layer)))
        assert.that(map.hashCode(),
                equalTo(KNAPSACK.database.map(map.layer).hashCode()))

        map["bar"] = "4"
        assert.that(map.keys, equalTo(setOf("foo", "bar")))
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = DatabaseTestRule()
    }
}