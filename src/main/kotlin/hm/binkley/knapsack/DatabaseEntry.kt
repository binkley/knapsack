package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseEntry(override val key: String, private val loader: SQLLoader)
    : MutableEntry<String, String?> {
    override val value: String?
        get() = loader.selectOne(key)

    override fun setValue(newValue: String?): String? {
        return loader.transaction {
            val previous = value
            if (null == newValue) {
                loader.deleteOne(key)
            } else {
                loader.upsertOne(key, newValue)
            }
            previous
        }
    }

    override fun equals(other: Any?): Boolean = kotlinEquals(other, properties)

    override fun hashCode() = Objects.hash(key)

    companion object {
        private val properties = arrayOf(DatabaseEntry::key)
    }
}
