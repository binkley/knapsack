package hm.binkley.knapsack

import org.eclipse.jgit.api.Git
import java.nio.file.Path
import java.sql.DriverManager.getConnection

class Knapsack(private val knapsackDir: Path) : AutoCloseable {
    private val database = getConnection("jdbc:hsqldb:file:$knapsackDir")

    fun init() {
        Git.init().
                setDirectory(knapsackDir.toFile()).
                call()
    }

    override fun close() {
        database.close()
    }
}
