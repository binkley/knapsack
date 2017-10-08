package hm.binkley.knapsack

class DatabaseEntryIterator(private val database: Database, val layer: Int)
    : MutableIterator<ValueEntry>
by database.selectLayerKeys(layer).map(
        { it -> database.entry(layer, it) },
        { it -> it.setValue(null) })
