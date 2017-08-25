package hm.binkley.knapsack

import java.sql.PreparedStatement
import java.util.Objects

class DatabaseSet(
        private val loader: SQLLoader,
        private val countAll: PreparedStatement,
        private val selectAll: PreparedStatement)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = DatabaseEntry(element.key, loader).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator() = DatabaseEntryIterator(loader, selectAll)

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
