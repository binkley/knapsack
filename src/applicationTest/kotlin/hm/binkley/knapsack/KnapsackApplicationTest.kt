package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

internal class KnapsackApplicationTest {
    @Rule
    @JvmField
    val tmpdir = TemporaryFolder()

    private lateinit var knapsackDir: File
    private lateinit var knapsack: Knapsack

    @Before
    fun setUp() {
        knapsackDir = tmpdir.newFolder()
        val database = Database.main(knapsackDir.absolutePath)
        knapsack = Knapsack(database)
    }

    @Test
    fun shouldInitRepo() {
        knapsack.init()

        assert.that(knapsackDir.resolve(".git").isDirectory, equalTo(true))
    }
}
