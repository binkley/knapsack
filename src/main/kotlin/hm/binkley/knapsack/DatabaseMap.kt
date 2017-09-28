package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals

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

    override fun equals(other: Any?)
            = kotlinEquals(other, properties) && super.equals(other)

    override fun hashCode() = 31 * layer + super.hashCode()

    companion object {
        private val properties = arrayOf(DatabaseMap::layer)
    }
}
