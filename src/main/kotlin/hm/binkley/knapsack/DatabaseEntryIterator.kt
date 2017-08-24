package hm.binkley.knapsack

import java.sql.PreparedStatement
import java.sql.ResultSet

class DatabaseEntryIterator(
        private val allResults: ResultSet,
        private val selectOne: PreparedStatement,
        private val upsertOne: PreparedStatement,
        private val deleteOne: PreparedStatement)
    : MutableIterator<Entry> {
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
            = DatabaseEntry(allResults.getString("key"),
            selectOne, upsertOne, deleteOne)
}
