package hm.binkley.knapsack

import java.util.Objects

class ValueSet(private val database: Database, val layer: Int)
    : AbstractMutableSet<ValueEntry>() {
    override val size: Int
        get() = database.countMap(layer)

    override fun iterator() = database.entryIterator(layer)

    override fun add(element: ValueEntry): Boolean {
        val newValue = element.value
        val previousValue = database.entry(layer, element.key).
                setValue(newValue)
        return !Objects.equals(previousValue, newValue)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueSet

        return layer == other.layer && super.equals(other)
    }

    override fun hashCode() = Objects.hash(layer)
}
