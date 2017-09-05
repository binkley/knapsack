package hm.binkley.knapsack

fun Database.entry(layer: Int, key: String) = DatabaseEntry(this, layer, key)

fun Database.set(layer: Int) = DatabaseSet(this, layer)

fun Database.map(layer: Int) = DatabaseMap(this, layer)
