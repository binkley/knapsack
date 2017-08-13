package hm.binkley.knapsack

import org.junit.Before
import org.junit.Test

internal class KnapsackTest {
    private lateinit var knapsack: Knapsack

    @Before
    fun setUpFixture() {
        knapsack = Knapsack()
    }

    @Test
    fun shouldStartEmpty() = knapsack.isEmpty() `is` true

    @Test
    fun shouldStartUnableToFindKeys()
            = knapsack.containsKey("foo") `is` false

    @Test
    fun shouldStartUnableToFetchValues() = knapsack["foo"] `is` null

    @Test
    fun shouldFetchDefaultValue() = knapsack.getOrDefault("foo", 3) `is` 3
}
