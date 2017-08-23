package hm.binkley.knapsack

import java.sql.PreparedStatement
import java.sql.ResultSet

class DatabaseSetIterator(
        private val selectOne: PreparedStatement,
        private val upsertOne: PreparedStatement,
        private val deleteOne: PreparedStatement,
        private val results: ResultSet)
    : MutableIterator<Entry> {
    override fun hasNext() = results.next()

    override fun next(): Entry {
        if (results.isBeforeFirst || results.isAfterLast)
            throw NoSuchElementException()
        return newDatabaseEntry()
    }

    override fun remove() {
        newDatabaseEntry().setValue(null)
        next() // Skip this element
    }

    private fun newDatabaseEntry()
            = DatabaseEntry(results.getString("key"),
            selectOne, upsertOne, deleteOne)
}
