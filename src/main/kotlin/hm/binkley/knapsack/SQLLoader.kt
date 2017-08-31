package hm.binkley.knapsack

import java.sql.PreparedStatement
import java.sql.SQLException

class SQLLoader(private val database: Database) : AutoCloseable {
    override fun close() = database.close()

    private val countAll: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("count-all").oneLine())
    }
    val selectAll: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("select-all").oneLine())
    }
    private val selectOne: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("select-one").oneLine())
    }
    val upsertOne: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("upsert-one").oneLine())
    }
    private val deleteOne: PreparedStatement by lazy {
        database.prepareStatement(SQLReader("delete-one").oneLine())
    }

    fun countAll(): Int {
        countAll.executeQuery().use { results ->
            if (!results.next()) throw IllegalStateException()
            val count = results.getInt("size")
            if (results.next()) throw IllegalStateException()
            return count
        }
    }

    fun selectOne(key: String): String? {
        selectOne.setString(1, key)
        selectOne.executeQuery().use { results ->
            if (!results.next()) return null
            val value = results.getString("value")
            if (results.next()) throw IllegalStateException()
            return value
        }
    }

    fun deleteOne(key: String) {
        deleteOne.setString(1, key)
        deleteOne.executeUpdate()
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
