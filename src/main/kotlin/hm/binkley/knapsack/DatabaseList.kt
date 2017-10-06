package hm.binkley.knapsack

class DatabaseList(private val database: Database)
    : AbstractList<ValueMap>() {
    override val size: Int
        get() = database.countList()

    override fun get(index: Int) = database.map(index)

    operator fun get(key: String) = map { it[key] }

    fun <T> get(key: String, rule: Rule<T>) = rule(key, this)
}
