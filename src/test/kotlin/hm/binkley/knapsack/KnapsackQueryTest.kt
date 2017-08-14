package hm.binkley.knapsack

import hm.binkley.knapsack.Knapsack.Layer
import org.jetbrains.exposed.sql.selectAll
import org.junit.Before
import org.junit.Test

internal class KnapsackQueryTest {
    private lateinit var knapsack: Knapsack

    @Before
    fun setUpFixture() {
        knapsack = Knapsack()
    }

    @Test
    fun shouldFindNothingWhenEmpty() {
        knapsack.execute {
            Layer.selectAll().empty() `is` true
        }
    }
}
