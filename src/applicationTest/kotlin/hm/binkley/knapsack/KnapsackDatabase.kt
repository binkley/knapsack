package hm.binkley.knapsack

import org.junit.rules.ExternalResource
import java.sql.Connection
import java.sql.DriverManager

class KnapsackDatabase : ExternalResource() {
    private lateinit var connection: Connection
    private lateinit var _loader: SQLLoader

    val loader
        get() = _loader

    fun reset() = object : ExternalResource() {
        override fun after() {
            _loader.reset()
        }
    }

    override fun before() {
        connection = DriverManager.getConnection(
                "jdbc:hsqldb:mem:knapsack")
        _loader = SQLLoader(connection)
        _loader.loadSchema()
    }

    override fun after() {
        connection.close()
    }
}
