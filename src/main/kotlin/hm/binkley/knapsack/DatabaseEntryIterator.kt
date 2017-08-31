package hm.binkley.knapsack

class DatabaseEntryIterator(private val database: Database)
    : MutableIterator<Entry>, AutoCloseable {
    private val results = database.selectAll()

    override fun hasNext() = results.next()

    override fun next(): Entry {
        if (results.isBeforeFirst || results.isAfterLast)
            throw NoSuchElementException()
        return newDatabaseEntry()
    }

    override fun remove() {
        if (results.isBeforeFirst || results.isAfterLast)
            throw IllegalStateException()
        // TODO: Detect remove() twice in a row without next() between
        newDatabaseEntry().setValue(null)
        next() // Skip this element
    }

    private fun newDatabaseEntry()
            = DatabaseEntry(results.getString("key"), database)

    override fun close() = results.close()
}
