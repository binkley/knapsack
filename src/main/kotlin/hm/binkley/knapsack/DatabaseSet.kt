package hm.binkley.knapsack

import java.util.Objects

class DatabaseSet(val layer: Int, private val database: Database)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = DatabaseEntry(layer, element.key, database).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator() = DatabaseEntryIterator(database)

    override val size: Int
        get() = database.countAll(layer)
}
