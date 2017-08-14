package hm.binkley.knapsack

import hm.binkley.knapsack.Knapsack.Layer
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class KnapsackQueryTest {
    private lateinit var knapsack: Knapsack

    @Before
    fun setUpFixture() {
        knapsack = Knapsack()
    }

    @After
    fun tearDownDatabase() {
        knapsack.execute {
            Layer.deleteAll()
        }
    }

    @Test
    fun shouldFindNothingWhenEmpty() {
        knapsack.execute {
            Layer.selectAll().empty() `is` true
        }
    }

    @Test
    fun shouldFindOne() {
        knapsack.execute {
            Layer.insert {
                it[key] = "foo"
                it[value] = 3.toString()
            }
            Layer.selectAll().count() `is` 1
        }
    }
}
