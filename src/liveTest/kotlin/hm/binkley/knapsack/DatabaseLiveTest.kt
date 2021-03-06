package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

internal class DatabaseLiveTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldCountListWhenEmpty() {
        assert.that(KNAPSACK.database.countList(), equalTo(0))
    }

    @Test
    fun shouldCountListWhenNonEmpty() {
        KNAPSACK.database.map(0)["foo"] = "3"

        assert.that(KNAPSACK.database.countList(), equalTo(1))
    }

    @Test
    fun shouldCountMapWhenEmpty() {
        assert.that(KNAPSACK.database.countMap(0), equalTo(0))
    }

    @Test
    fun shouldSelectOneWhenEmpty() {
        assert.that(KNAPSACK.database.selectOne(0, "foo"), absent())
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = DatabaseLiveTestRule()
    }
}
