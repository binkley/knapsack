package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

internal class DatabaseListLiveTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldWorkEndToEnd() {
        val list = KNAPSACK.database.list()

        assert.that(list.isEmpty(), equalTo(true))
        assert.that(list.size, equalTo(0))

        val map = KNAPSACK.database.map(0)
        map["foo"] = "3"

        assert.that(list.isEmpty(), equalTo(false))
        assert.that(list.size, equalTo(1))

        assert.that(list, equalTo(listOf(map)))
        assert.that(list.hashCode(), equalTo(listOf(map).hashCode()))
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = DatabaseLiveTestRule()
    }
}
