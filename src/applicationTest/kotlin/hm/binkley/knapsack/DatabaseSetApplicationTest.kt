package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import java.util.AbstractMap.SimpleEntry
import kotlin.test.fail

internal class DatabaseSetApplicationTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldWorkEndToEnd() {
        val set = DatabaseSet(KNAPSACK.loader)

        assert.that(set.isEmpty(), equalTo(true))
        assert.that(set.add(SimpleEntry("foo", "3")), equalTo(true))
        assert.that(set.add(SimpleEntry("foo", "3")), equalTo(false))
        assert.that(set.add(SimpleEntry("foo", "7")), equalTo(true))
        assert.that(set.size, equalTo(1))
        assert.that(set.add(SimpleEntry("bar", "3")), equalTo(true))
        assert.that(set.add(SimpleEntry("bar", "3")), equalTo(false))
        assert.that(set.add(SimpleEntry("bar", "15")), equalTo(true))
        assert.that(set.size, equalTo(2))

        set.iterator().use { sit ->
            while (sit.hasNext()) {
                val e = sit.next()
                when (e.key) {
                    "foo" -> {
                        assert.that(e.value, equalTo("7"))
                        sit.remove()
                    }
                    "bar" -> assert.that(e.value, equalTo("15"))
                    else -> fail()
                }
            }
        }

        assert.that(set.size, equalTo(1))
        assert.that(set.map { it.key }.first(), equalTo("bar"))
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = DatabaseTestRule()
    }
}
