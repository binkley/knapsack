package hm.binkley.knapsack

import org.junit.Test

internal class KnapsackTest {
    @Test
    fun shouldStartEmpty() = Knapsack().isEmpty() `is` true
}
