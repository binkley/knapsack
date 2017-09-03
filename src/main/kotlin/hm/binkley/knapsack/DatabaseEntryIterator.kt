package hm.binkley.knapsack

class DatabaseEntryIterator(val layer: Int, private val database: Database)
    : MutableIterator<Entry>, AutoCloseable {
    private val results = database.selectKeys(layer)
    private var lastRemoveIndex = 0

    override fun hasNext() = results.next()

    override fun next(): Entry {
        if (results.isBeforeFirst || results.isAfterLast)
            throw NoSuchElementException()
        return newDatabaseEntry()
    }

    override fun remove() {
        if (results.isBeforeFirst || results.isAfterLast)
            throw IllegalStateException()

        val row = results.row
        if (row == lastRemoveIndex)
            throw IllegalStateException()
        lastRemoveIndex = row

        newDatabaseEntry().setValue(null)
    }

    private fun newDatabaseEntry()
            = DatabaseEntry(layer, results.getString("key"), database)

    override fun close() = results.close()
}
