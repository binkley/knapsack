package hm.binkley.knapsack

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class SQLLoader(private val database: Connection) : AutoCloseable {
    override fun close() = database.close()

    val countAll: PreparedStatement by lazy {
        database.prepareStatement(readSql("count-all"))
    }
    val selectAll: PreparedStatement by lazy {
        database.prepareStatement(readSql("select-all"))
    }
    val selectOne: PreparedStatement by lazy {
        database.prepareStatement(readSql("select-one"))
    }
    val upsertOne: PreparedStatement by lazy {
        database.prepareStatement(readSql("upsert-one"))
    }
    val deleteOne: PreparedStatement by lazy {
        database.prepareStatement(readSql("delete-one"))
    }

    fun loadSchema() {
        database.createStatement().use {
            it.executeUpdate(readSql("schema"))
        }
    }

    fun reset() {
        database.createStatement().use {
            it.executeUpdate(readSql("delete-all"))
        }
    }

    fun <T> transaction(block: () -> T): T {
        database.autoCommit = false
        try {
            val value = block()
            database.commit()
            return value
        } catch (e: SQLException) {
            database.rollback()
            throw e
        } finally {
            database.autoCommit = true
        }
    }

    private fun readSql(purpose: String): String {
        return javaClass.
                getResource("/hm/binkley/knapsack/knapsack-$purpose.sql").
                readText()
    }
}
