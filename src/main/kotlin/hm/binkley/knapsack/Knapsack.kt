package hm.binkley.knapsack

import org.eclipse.jgit.api.Git
import java.io.File

class Knapsack(private val database: Database) : AutoCloseable {
    fun init() {
        Git.init().
                setDirectory(File(database.knapsackDir)).
                call()
    }

    override fun close() {
        database.close()
    }
}
