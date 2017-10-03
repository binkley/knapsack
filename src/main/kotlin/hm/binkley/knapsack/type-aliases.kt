package hm.binkley.knapsack

import kotlin.collections.MutableMap.MutableEntry

typealias Entry = MutableEntry<String, Value>

typealias Rule<T> = (String, DatabaseList) -> T
