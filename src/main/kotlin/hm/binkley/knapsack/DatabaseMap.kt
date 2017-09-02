package hm.binkley.knapsack

class DatabaseMap(private val database: Database)
    : AbstractMutableMap<String, String?>() {
    override val entries: MutableSet<Entry> = DatabaseSet(0, database)

    override fun put(key: String, value: String?)
            = DatabaseEntry(0, key, database).setValue(value)
}
