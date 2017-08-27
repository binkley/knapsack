package hm.binkley.knapsack

import org.junit.rules.ExternalResource
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.DriverManager.getConnection

class KnapsackDatabaseTestRule : ExternalResource() {
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
        val database = getConnection(
                "jdbc:hsqldb:file:$knapsackDir/knapsack/db")
        database.transactionIsolation = TRANSACTION_SERIALIZABLE
        _loader = SQLLoader(database)
        _loader.loadSchema()
    }

    override fun after() {
        _loader.close()
    }
}
