package hm.binkley.knapsack

import java.util.Objects

class DatabaseMap(private val database: Database, val layer: Int)
    : AbstractMutableMap<String, String?>() {
    init {
        if (0 > layer) throw IndexOutOfBoundsException("Layer: $layer")
        if (database.countList() < layer)
            throw IndexOutOfBoundsException("Layer: $layer")
    }

    override val entries: MutableSet<Entry> = database.set(layer)

    override fun put(key: String, value: String?)
            = database.entry(layer, key).setValue(value)

    override fun remove(key: String): String? {
        val oldValue = get(key)
        if (null != oldValue)
            database.deleteOne(layer, key)
        return oldValue
    }

    override fun remove(key: String, value: String?): Boolean {
        val oldValue = get(key)
        if (oldValue == value && null != oldValue) {
            database.deleteOne(layer, key)
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseMap

        return layer == other.layer && super.equals(other)
    }

    override fun hashCode() = Objects.hash(layer)
}
