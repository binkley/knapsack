package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals
import java.util.Objects

class DatabaseSet(private val database: Database, val layer: Int)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = database.entry(layer, element.key).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun iterator() = database.entryIterator(layer)

    override val size: Int
        get() = database.countMap(layer)

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
