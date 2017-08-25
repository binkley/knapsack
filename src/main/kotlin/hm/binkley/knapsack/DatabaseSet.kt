package hm.binkley.knapsack

import java.util.Objects

class DatabaseSet(private val loader: SQLLoader)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = DatabaseEntry(element.key, loader).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator() = DatabaseEntryIterator(loader)

    override val size: Int
        get() {
            val countAll = loader.countAll
            countAll.executeQuery().use { results ->
                if (!results.next()) throw IllegalStateException()
                val size = results.getInt("size")
                if (results.next()) throw IllegalStateException()
                return size
            }
        }
}
