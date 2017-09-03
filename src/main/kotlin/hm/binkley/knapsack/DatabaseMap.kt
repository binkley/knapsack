package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals

class DatabaseMap(val layer: Int, private val database: Database)
    : AbstractMutableMap<String, String?>() {
    override val entries: MutableSet<Entry> = DatabaseSet(layer, database)

    override fun put(key: String, value: String?)
            = DatabaseEntry(layer, key, database).setValue(value)

    override fun equals(other: Any?): Boolean {
        if (!kotlinEquals(other, properties))
            return false;
        return super.equals(other)
    }

    override fun hashCode() = 31 * layer + super.hashCode()

    companion object {
        private val properties = arrayOf(DatabaseMap::layer)
    }
}
