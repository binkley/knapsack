package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals

class DatabaseMapList(private val database: Database)
    : AbstractMutableMap<String, String?>() {
    private val layers: MutableList<DatabaseMap> = ArrayList() // TODO: queue?
    private var current: DatabaseMap = database.map(0)

    override val entries
        get() = current.entries

    override fun put(key: String, value: String?) = current.put(key, value)

    override fun equals(other: Any?) = kotlinEquals(other, properties)

    override fun hashCode() = layers.hashCode()

    val layer
        get() = current.layer

    fun next() {
        current = database.map(current.layer + 1)
        layers.add(current)
    }

    init {
        layers.add(current)
    }

    companion object {
        private val properties = arrayOf(DatabaseMapList::layers)
    }
}
