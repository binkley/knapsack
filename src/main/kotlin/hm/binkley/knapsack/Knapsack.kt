package hm.binkley.knapsack

class Knapsack(private val database: Database) : AutoCloseable {
    override fun close() {
        database.close()
    }
}
