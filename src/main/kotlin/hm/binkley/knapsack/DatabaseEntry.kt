package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseEntry(
        private val database: Database,
        val layer: Int,
        override val key: String)
    : MutableEntry<String, String?> {
    override val value: String?
        get() = database.selectOne(layer, key)

    override fun setValue(newValue: String?): String? {
        return database.transaction {
            val previous = value
            when (newValue) {
                previous -> {
                }
                null -> database.deleteOne(layer, key)
                else -> database.upsertOne(layer, key, newValue)
            }
            previous
        }
    }

    override fun equals(other: Any?) = kotlinEquals(other, properties)

    override fun hashCode() = Objects.hash(layer, key)

    companion object {
        private val properties
                = arrayOf(DatabaseEntry::layer, DatabaseEntry::key)
    }
}
