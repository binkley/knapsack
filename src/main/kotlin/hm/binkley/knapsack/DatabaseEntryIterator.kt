package hm.binkley.knapsack

import java.sql.PreparedStatement

class DatabaseEntryIterator(
        private val loader: SQLLoader,
        selectAll: PreparedStatement)
    : MutableIterator<Entry>, AutoCloseable {
    private val allResults = selectAll.executeQuery()

    override fun hasNext() = allResults.next()

    override fun next(): Entry {
        if (allResults.isBeforeFirst || allResults.isAfterLast)
            throw NoSuchElementException()
        return newDatabaseEntry()
    }

    override fun remove() {
        newDatabaseEntry().setValue(null)
        next() // Skip this element
    }

    private fun newDatabaseEntry()
            = DatabaseEntry(allResults.getString("key"), loader)

    override fun close() = allResults.close()
}
