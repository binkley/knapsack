package hm.binkley.knapsack

import org.junit.rules.ExternalResource
import java.sql.Connection
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.DriverManager

class KnapsackDatabase : ExternalResource() {
    private lateinit var _database: Connection
    private lateinit var _loader: SQLLoader

    val database
        get() = _database
    val loader
        get() = _loader

    fun reset() = object : ExternalResource() {
        override fun after() {
            _loader.reset()
        }
    }

    override fun before() {
        _database = DriverManager.getConnection("jdbc:hsqldb:mem:knapsack")
        _database.transactionIsolation = TRANSACTION_SERIALIZABLE
        _loader = SQLLoader(_database)
        _loader.loadSchema()
    }

    override fun after() {
        _database.close()
    }
}
