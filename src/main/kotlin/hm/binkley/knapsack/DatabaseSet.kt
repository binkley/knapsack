package hm.binkley.knapsack

import java.sql.Connection
import java.sql.PreparedStatement
import java.util.Objects

class DatabaseSet(
        private val database: Connection,
        private val countAll: PreparedStatement,
        private val selectAll: PreparedStatement,
        private val selectOne: PreparedStatement,
        private val upsertOne: PreparedStatement,
        private val deleteOne: PreparedStatement)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue
                = DatabaseEntry(element.key, database, selectOne, upsertOne,
                deleteOne).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator()
            = DatabaseEntryIterator(database, selectAll, selectOne, upsertOne,
            deleteOne)

    override val size: Int
        get() {
            countAll.executeQuery().use { results ->
                if (!results.next()) throw IllegalStateException()
                val size = results.getInt("size")
                if (results.next()) throw IllegalStateException()
                return size
            }
        }
}
