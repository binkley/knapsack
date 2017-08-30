package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class DatabaseEntry(override val key: String, private val loader: SQLLoader)
    : MutableEntry<String, String?> {
    override val value: String?
        get() {
            val selectOne = loader.selectOne
            selectOne.setString(1, key)
            selectOne.executeQuery().use { results ->
                if (!results.next()) return null
                val value = results.getString("value")
                if (results.next()) throw IllegalStateException()
                return value
            }
        }

    override fun setValue(newValue: String?): String? {
        return loader.transaction {
            val previous = value
            if (null == newValue) {
                val deleteOne = loader.deleteOne
                deleteOne.setString(1, key)
                deleteOne.executeUpdate()
            } else {
                val upsertOne = loader.upsertOne
                upsertOne.setString(1, key)
                upsertOne.setString(2, newValue)
                upsertOne.executeUpdate()
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
