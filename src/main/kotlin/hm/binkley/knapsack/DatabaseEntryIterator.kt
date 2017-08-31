package hm.binkley.knapsack

class DatabaseEntryIterator(private val loader: SQLLoader)
    : MutableIterator<Entry>, AutoCloseable {
    private val allResults = loader.selectAll.executeQuery()

    override fun hasNext() = allResults.next()

    override fun next(): Entry {
        if (allResults.isBeforeFirst || allResults.isAfterLast)
            throw NoSuchElementException()
        return newDatabaseEntry()
    }

    override fun remove() {
        if (allResults.isBeforeFirst || allResults.isAfterLast)
            throw IllegalStateException()
        // TODO: Detect remove() twice in a row without next() between
        newDatabaseEntry().setValue(null)
        next() // Skip this element
    }

    private fun newDatabaseEntry()
            = DatabaseEntry(allResults.getString("key"), loader)

    override fun close() = allResults.close()
}
