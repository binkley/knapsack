package hm.binkley.knapsack

import java.sql.PreparedStatement
import java.sql.SQLException

class SQLLoader(private val database: Database) : AutoCloseable {
    override fun close() = database.close()

    val countAll: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("count-all").oneLine())
    }
    val selectAll: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("select-all").oneLine())
    }
    val selectOne: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("select-one").oneLine())
    }
    val upsertOne: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("upsert-one").oneLine())
    }
    val deleteOne: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("delete-one").oneLine())
    }

    fun loadSchema() {
        database.createStatement().use { statement ->
            SQLReader("schema").lines().
                    forEach { statement.executeUpdate(it) }
        }
    }

    fun reset() {
        database.createStatement().use {
            it.executeUpdate(SQLReader("delete-all").oneLine())
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
}
