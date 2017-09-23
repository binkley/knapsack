package hm.binkley.knapsack

class DatabaseEntryIterator(private val database: Database, val layer: Int)
    : MutableIterator<Entry>, AutoCloseable {
    private val results = database.selectMapKeys(layer)
    private var hasNext = false
    private var next = false

    override fun hasNext(): Boolean {
        hasNext = results.next()
        next = false
        return hasNext
    }

    override fun next(): Entry {
        if (!hasNext)
            throw NoSuchElementException()
        hasNext = false
        next = true
        return entry()
    }

    override fun remove() {
        if (!next)
            throw IllegalStateException()
        next = false

        entry().setValue(null)
    }

    override fun close() = results.close()

    private fun entry() = database.entry(layer, results.getString("key"))
}
