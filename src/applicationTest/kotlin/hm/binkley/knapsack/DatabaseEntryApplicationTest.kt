package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test

class DatabaseEntryApplicationTest {
    @Rule
    @JvmField
    val reset = KNAPSACK.reset()

    @Test
    fun shouldWorkEndToEnd() {
        val selectOne = KNAPSACK.loader.prepareSelectOne
        val upsertOne = KNAPSACK.loader.prepareUpsertOne
        val deleteOne = KNAPSACK.loader.prepareDeleteOne

        val entry = DatabaseEntry("foo", selectOne, upsertOne, deleteOne)

        assert.that(entry.value, absent())
        assert.that(entry.setValue("3"), absent())
        assert.that(entry.setValue("4"), equalTo("3"))
        assert.that(entry.value, equalTo("4"))
    }

    companion object {
        @ClassRule
        @JvmField
        val KNAPSACK = KnapsackDatabase()
    }
}