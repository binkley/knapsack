package hm.binkley.knapsack

class Ruck(database: Database) {
    private val maps = DatabaseList(database)
    private val rules = HashMap<String, Rule<Any>>()
    private val cache = HashMap<String, Any>()

    operator fun set(key: String, rule: Rule<Any>) {
        rules.put(key, rule)
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String)
            = (cache as MutableMap<String, T>).getOrPut(key) {
        (rules[key] as Rule<T>)(key, maps)
    }
}
