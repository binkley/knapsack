package hm.binkley.knapsack

class DatabaseList(private val database: Database)
    : AbstractList<DatabaseMap>() {
    override val size: Int
        get() = database.countList()

    override fun get(index: Int) = database.map(index)

    operator fun get(key: String) = map { it[key] }
}
