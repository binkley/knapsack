package hm.binkley.knapsack

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
                previous -> Unit
                null -> database.deleteOne(layer, key)
                else -> database.upsertOne(layer, key, newValue)
            }
            previous
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseEntry

        return layer == other.layer && key == other.key
    }

    override fun hashCode() = Objects.hash(layer, key)
}
