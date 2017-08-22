package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.sql.PreparedStatement
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseMapEntry(
        override val key: String,
        private val select: PreparedStatement,
        private val insert: PreparedStatement,
        private val delete: PreparedStatement) : MutableEntry<String, String?> {
    override val value: String?
        get() {
            select.setString(1, key)
            val results = select.executeQuery()
            if (!results.next()) return null
            return results.getString("value")
        }

    override fun setValue(newValue: String?): String? {
        // TODO: Transaction so get/set does not mutate in between
        val previous = value
        if (null == value) {
            delete.setString(1, key)
            delete.executeUpdate()
        } else {
            insert.setString(1, key)
            insert.setString(2, value)
            insert.executeUpdate()
        }
        return previous
    }

    override fun equals(other: Any?) = kotlinEquals(other, properties)

    override fun hashCode() = Objects.hash(key)

    companion object {
        private val properties = arrayOf(DatabaseMapEntry::key)
    }
}
