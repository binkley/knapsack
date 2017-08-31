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
        get() = loader.countAll()
}
