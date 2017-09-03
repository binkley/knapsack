package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

internal class KnapsackLiveTest {
    @Rule
    @JvmField
    val tmpdir = TemporaryFolder()

    private lateinit var knapsackDir: File
    private lateinit var knapsack: Knapsack

    @Before
    fun setUp() {
        knapsackDir = tmpdir.newFolder()
        knapsack = Knapsack(knapsackDir.toPath())
    }

    @After
    fun tearDown() {
        knapsack.close()
    }

    @Test
    fun shouldClose() {
        knapsack.use {}
    }

    @Test
    fun shouldInitRepo() {
        assert.that(knapsackDir.resolve(".git").isDirectory, equalTo(true))
    }
}
