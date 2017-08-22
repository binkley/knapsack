package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.present
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class KnapsackMapTest {
    private lateinit var knapsack: Knapsack

    @Before
    fun setUpKnapsack() {
        knapsack = Knapsack()
    }

    @After
    fun tearDownKnapsack() {
        knapsack.close()
    }

    @Test
    fun shouldDoNothing() {
        assert.that(knapsack, present())
    }
}
