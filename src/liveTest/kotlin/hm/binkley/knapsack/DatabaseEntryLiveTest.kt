package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

internal class DatabaseEntryLiveTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldWorkEndToEnd() {
        val entry = KNAPSACK.database.entry(0, "foo")

        assert.that(entry.value, absent())
        assert.that(entry.setValue("3"), absent())
        assert.that(entry.setValue("4"), equalTo("3"))
        assert.that(entry.value, equalTo("4"))
    }

    @Test
    fun shouldPersist() {
        val layer = 0
        val key = "foo"
        KNAPSACK.database.clone().use { database ->
            database.entry(layer, key).setValue("2")
        }

        val entry = KNAPSACK.database.entry(layer, key)

        assertThat(entry.value, equalTo("2"))
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = DatabaseTestRule()
    }
}