package hm.binkley.knapsack

import org.junit.Before
import org.junit.Test

internal class KnapsackMapTest {
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

    @Test
    fun shouldRememberAssignment() {
        knapsack["foo"] = 3
        knapsack["foo"] `is` 3
    }

    @Test
    fun shouldRemoveThingsUnconditionally() {
        knapsack["foo"] = 3
        knapsack.remove("foo")
        knapsack.containsKey("foo") `is` false
    }

    @Test
    fun shouldRemoveTheRightThings() {
        knapsack["foo"] = 3
        knapsack.remove("foo", 3)
        knapsack.containsKey("foo") `is` false
    }

    @Test
    fun shouldNotRemoveTheWrongThings() {
        knapsack["foo"] = 3
        knapsack.remove("foo", 4)
        knapsack["foo"] `is` 3
    }
}
