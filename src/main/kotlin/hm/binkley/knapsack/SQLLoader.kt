package hm.binkley.knapsack

class SQLLoader {
    fun schema()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-schema.sql").
            readText()

    fun countAll()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-count-all.sql").
            readText()

    fun selectAll()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-select-all.sql").
            readText()

    fun selectOne()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-select-one.sql").
            readText()

    fun upsertOne()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-upsert-one.sql").
            readText()

    fun deleteOne()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-delete-one.sql").
            readText()

    fun deleteAll()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-delete-all.sql").
            readText()
}
