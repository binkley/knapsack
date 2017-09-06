package hm.binkley.knapsack

import au.com.console.kassava.kotlinEquals

class DatabaseMapList(private val database: Database)
    : AbstractMutableMap<String, String?>() {
    private val _layers: MutableList<DatabaseMap> = ArrayList() // TODO: queue?
    private var current: DatabaseMap = database.map(0)

    override val entries
        get() = current.entries

    override fun put(key: String, value: String?) = current.put(key, value)

    override fun equals(other: Any?) = kotlinEquals(other, properties)

    override fun hashCode() = _layers.hashCode()

    val layers: List<DatabaseMap>
        get() = _layers

    val layer
        get() = current.layer

    fun next() {
        current = database.map(current.layer + 1)
        _layers.add(current)
    }

    init {
        _layers.add(current)
    }

    companion object {
        private val properties = arrayOf(DatabaseMapList::_layers)
    }
}
