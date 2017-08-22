package hm.binkley.knapsack

import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
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
    fun shouldDoNothing() = assertThat(knapsack, notNullValue())
}
