package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.sql.PreparedStatement
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseEntry(
        override val key: String,
        private val selectOne: PreparedStatement,
        private val upsertOne: PreparedStatement,
        private val deleteOne: PreparedStatement)
    : MutableEntry<String, String?> {
    override val value: String?
        get() {
            selectOne.setString(1, key)
            val results = selectOne.executeQuery()
            if (!results.next()) return null
            val value = results.getString(VALUE_COLUMN)
            if (results.next()) throw IllegalStateException()
            return value
        }

    override fun setValue(newValue: String?): String? {
        // TODO: Transaction so get/set does not mutate in between
        val previous = value
        if (null == newValue) {
            deleteOne.setString(1, key)
            deleteOne.executeUpdate()
        } else {
            upsertOne.setString(1, key)
            upsertOne.setString(2, newValue)
            upsertOne.executeUpdate()
        }
        return previous
    }

    override fun equals(other: Any?): Boolean {
        return kotlinEquals(other, properties)
    }

    override fun hashCode() = Objects.hash(key)

    companion object {
        const val VALUE_COLUMN = "value"
        val properties = arrayOf(DatabaseEntry::key)
    }
}
