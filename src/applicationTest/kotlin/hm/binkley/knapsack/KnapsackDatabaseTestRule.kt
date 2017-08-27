package hm.binkley.knapsack

import org.junit.rules.ExternalResource
import java.sql.Connection
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.DriverManager.getConnection

class KnapsackDatabaseTestRule : ExternalResource() {
    private lateinit var _database: Connection
    private lateinit var _loader: SQLLoader

    val loader
        get() = _loader

    fun reset() = object : ExternalResource() {
        override fun after() {
            _loader.reset()
        }
    }

    override fun before() {
        val knapsackDir = System.getProperty("java.io.tmpdir")
        _database = getConnection("jdbc:hsqldb:file:$knapsackDir/knapsack/db")
        _database.transactionIsolation = TRANSACTION_SERIALIZABLE
        _loader = SQLLoader(_database)
        _loader.loadSchema()
    }

    override fun after() {
        _loader.close()
    }
}
