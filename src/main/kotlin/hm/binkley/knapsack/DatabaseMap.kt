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

    override fun equals(other: Any?): Boolean {
        if (!kotlinEquals(other, properties))
            return false
        return super.equals(other)
    }

    override fun hashCode() = 31 * layer + super.hashCode()

    companion object {
        private val properties = arrayOf(DatabaseMap::layer)
    }
}
