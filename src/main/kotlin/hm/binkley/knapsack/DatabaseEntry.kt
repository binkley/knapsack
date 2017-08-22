package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.sql.PreparedStatement
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseEntry(
        override val key: String,
        private val select: PreparedStatement,
        private val upsert: PreparedStatement,
        private val delete: PreparedStatement) : MutableEntry<String, String?> {
    override val value: String?
        get() {
            select.setString(1, key)
            val results = select.executeQuery()
            if (!results.next()) return null
            val value = results.getString(VALUE_COLUMN)
            if (results.next()) throw IllegalStateException()
            return value
        }

    override fun setValue(newValue: String?): String? {
        // TODO: Transaction so get/set does not mutate in between
        val previous = value
        if (null == newValue) {
            delete.setString(1, key)
            delete.executeUpdate()
        } else {
            upsert.setString(1, key)
            upsert.setString(2, newValue)
            upsert.executeUpdate()
        }
        return previous
    }

    override fun equals(other: Any?)
            = kotlinEquals(other, arrayOf(DatabaseEntry::key))

    override fun hashCode() = Objects.hash(key)

    companion object {
        const val VALUE_COLUMN = "value"
    }
}
