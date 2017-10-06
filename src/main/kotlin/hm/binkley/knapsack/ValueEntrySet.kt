package hm.binkley.knapsack

import java.util.Objects

class ValueEntrySet(private val database: Database, val layer: Int)
    : AbstractMutableSet<Entry>() {
    override fun add(element: Entry): Boolean {
        val newValue = element.value
        val previousValue = database.entry(layer, element.key).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override val size: Int
        get() = database.countMap(layer)

    override fun iterator() = database.entryIterator(layer)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueEntrySet

        return layer == other.layer && super.equals(other)
    }

    override fun hashCode() = Objects.hash(layer)
}
