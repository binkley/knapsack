package hm.binkley.knapsack

import org.junit.rules.ExternalResource
import java.sql.DriverManager.getConnection

class DatabaseTestRule : ExternalResource() {
    private lateinit var _database: Database

    val database
        get() = _database

    override fun before() {
        _database = Database(
                getConnection("jdbc:hsqldb:file:${System.getProperty(
                        "java.io.tmpdir")}"))
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
