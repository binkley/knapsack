package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseEntry(
        override val key: String,
        private val database: Connection,
        private val selectOne: PreparedStatement,
        private val upsertOne: PreparedStatement,
        private val deleteOne: PreparedStatement)
    : MutableEntry<String, String?> {
    override val value: String?
        get() {
            selectOne.setString(1, key)
            selectOne.executeQuery().use { results ->
                if (!results.next()) return null
                val value = results.getString("value")
                if (results.next()) throw IllegalStateException()
                return value
            }
        }

    override fun setValue(newValue: String?): String? {
        database.autoCommit = false
        try {
            val previous = value
            if (null == newValue) {
                deleteOne.setString(1, key)
                deleteOne.executeUpdate()
            } else {
                upsertOne.setString(1, key)
                upsertOne.setString(2, newValue)
                upsertOne.executeUpdate()
            }
            database.commit()
            return previous
        } catch (e: SQLException) {
            database.rollback()
            throw e
        } finally {
            database.autoCommit = true
        }
    }

    override fun equals(other: Any?): Boolean = kotlinEquals(other, properties)

    override fun hashCode() = Objects.hash(key)

    companion object {
        val properties = arrayOf(DatabaseEntry::key)
    }
}
