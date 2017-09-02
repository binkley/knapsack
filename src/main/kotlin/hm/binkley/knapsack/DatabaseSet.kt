package hm.binkley.knapsack

import java.util.Objects

class DatabaseSet(private val database: Database)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = DatabaseEntry(element.key, database).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator() = DatabaseEntryIterator(database)

    override val size: Int
        get() = database.countAll(0)
}
