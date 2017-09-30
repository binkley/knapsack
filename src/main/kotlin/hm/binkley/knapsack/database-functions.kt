package hm.binkley.knapsack

import hm.binkley.knapsack.Value.DatabaseValue

fun Database.value(layer: Int, key: String)
        = DatabaseValue(this, layer, key)

fun Database.entry(layer: Int, key: String) = DatabaseEntry(this, layer, key)

fun Database.entryIterator(layer: Int) = DatabaseEntryIterator(this, layer)

fun Database.set(layer: Int) = DatabaseSet(this, layer)

fun Database.map(layer: Int) = DatabaseMap(this, layer)

fun Database.list() = DatabaseList(this)
