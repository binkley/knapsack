package hm.binkley.knapsack

import hm.binkley.knapsack.Value.DatabaseValue
import hm.binkley.knapsack.Value.NoValue

fun Database.value(layer: Int, key: String, value: String)
        = DatabaseValue(this, layer, key, value)

fun Database.value(layer: Int, key: String): Value {
    val value = selectOne(layer, key)
    return if (null == value) NoValue else value(layer, key, value)
}

fun Database.entry(layer: Int, key: String, value: Value)
        = ValueEntry(this, layer, key, value)

fun Database.entry(layer: Int, key: String)
        = entry(layer, key, value(layer, key))

fun Database.databaseEntry(layer: Int, key: String) = DatabaseEntry(this,
        layer, key)

fun Database.entryIterator(layer: Int) = DatabaseEntryIterator(this, layer)

fun Database.set(layer: Int) = DatabaseSet(this, layer)

fun Database.map(layer: Int) = DatabaseMap(this, layer)

fun Database.list() = DatabaseList(this)
