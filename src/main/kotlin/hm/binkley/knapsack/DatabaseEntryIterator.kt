package hm.binkley.knapsack

class DatabaseEntryIterator(private val database: Database, val layer: Int)
    : MutableIterator<Entry>, AutoCloseable {
    private val results = database.selectMapKeys(layer)
    private var lastRemoveIndex = 0

    override fun hasNext() = results.next()

    override fun next(): Entry {
        if (results.isBeforeFirst || results.isAfterLast)
            throw NoSuchElementException()
        return entry()
    }

    override fun remove() {
        if (results.isBeforeFirst || results.isAfterLast)
            throw IllegalStateException()

        val row = results.row
        if (row == lastRemoveIndex)
            throw IllegalStateException()
        lastRemoveIndex = row

        entry().setValue(null)
    }

    override fun close() = results.close()

    private fun entry() = database.entry(layer, results.getString("key"))
}
