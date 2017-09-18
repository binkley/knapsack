package hm.binkley.knapsack

class DatabaseEntryIterator(private val database: Database, val layer: Int)
    : ResultSetIterator<Entry>(database.selectMapKeys(layer),
        { results ->
            database.entry(layer, results.getString("key"))
        },
        { results ->
            database.entry(layer, results.getString("key")).setValue(null)
        })
