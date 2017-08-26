package hm.binkley.knapsack

class DatabaseMap(private val loader: SQLLoader)
    : AbstractMutableMap<String, String?>() {
    override val entries: MutableSet<Entry> = DatabaseSet(loader)

    override fun put(key: String, value: String?)
            = DatabaseEntry(key, loader).setValue(value)
}
