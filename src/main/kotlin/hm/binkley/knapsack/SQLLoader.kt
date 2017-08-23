package hm.binkley.knapsack

import java.sql.Connection

class SQLLoader(private val connection: Connection) {
    fun loadSchema() {
        connection.createStatement().use {
            it.executeUpdate(schema())
        }
    }

    fun reset() {
        connection.createStatement().use {
            it.executeUpdate(deleteAll())
        }
    }

    fun prepareCountAll() = connection.prepareStatement(countAll())
    fun prepareSelectAll() = connection.prepareStatement(selectAll())
    fun prepareSelectOne() = connection.prepareStatement(selectOne())
    fun prepareUpsertOne() = connection.prepareStatement(upsertOne())
    fun prepareDeleteOne() = connection.prepareStatement(deleteOne())

    private fun schema()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-schema.sql").
            readText()

    private fun countAll()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-count-all.sql").
            readText()

    private fun selectAll()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-select-all.sql").
            readText()

    private fun selectOne()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-select-one.sql").
            readText()

    private fun upsertOne()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-upsert-one.sql").
            readText()

    private fun deleteOne()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-delete-one.sql").
            readText()

    private fun deleteAll()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-delete-all.sql").
            readText()
}
