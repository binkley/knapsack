package hm.binkley.knapsack

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class KnapsackTest {
    @Test
    fun shouldStartEmpty() = assertThat(Knapsack().isEmpty(), `is`(true))
}
