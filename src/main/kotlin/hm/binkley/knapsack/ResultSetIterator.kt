package hm.binkley.knapsack

import java.sql.ResultSet

abstract class ResultSetIterator<out T>(
        private val results: ResultSet,
        private val ctor: (ResultSet) -> T,
        private val dtor: (ResultSet) -> Unit)
    : MutableIterator<T>, AutoCloseable {
    private var lastRemoveIndex = 0

    override final fun hasNext() = results.next()

    override final fun next(): T {
        if (results.isBeforeFirst || results.isAfterLast)
            throw NoSuchElementException()
        return ctor(results)
    }

    override final fun remove() {
        if (results.isBeforeFirst || results.isAfterLast)
            throw IllegalStateException()

        val row = results.row
        if (row == lastRemoveIndex)
            throw IllegalStateException()
        lastRemoveIndex = row

        dtor(results)
    }

    override final fun close() = results.close()
}
