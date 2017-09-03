package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.util.Objects

class DatabaseSet(val layer: Int, private val database: Database)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = DatabaseEntry(layer, element.key, database).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator() = DatabaseEntryIterator(layer, database)

    override val size: Int
        get() = database.countAll(layer)

    override fun equals(other: Any?): Boolean {
        if (!kotlinEquals(other, properties))
            return false;
        return super.equals(other)
    }

    override fun hashCode() = 31 * layer + super.hashCode()

    companion object {
        private val properties = arrayOf(DatabaseSet::layer)
    }
}
