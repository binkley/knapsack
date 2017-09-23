package hm.binkley.knapsack

import org.eclipse.jgit.api.Git
import java.nio.file.Path
import java.sql.DriverManager.getConnection

class Knapsack(knapsackDir: Path) : AutoCloseable {
    private val database = getConnection("jdbc:hsqldb:file:$knapsackDir")

    init {
        Git.init().
                setDirectory(knapsackDir.toFile()).
                call()
    }

    override fun close() {
        database.close()
    }
}
