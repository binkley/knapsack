package hm.binkley.knapsack

import org.junit.rules.ExternalResource
import java.nio.file.Files.createTempDirectory
import java.sql.DriverManager.getConnection

class DatabaseLiveTestRule : ExternalResource() {
    private lateinit var _database: Database

    val database
        get() = _database

    override fun before() {
        _database = Database(getConnection(
                "jdbc:hsqldb:file:${createTempDirectory("knapsack")}"))
        _database.loadSchema()
    }

    override fun after() {
        _database.close()
    }

    fun reset() = object : ExternalResource() {
        override fun after() {
            _database.reset()
        }
    }
}
