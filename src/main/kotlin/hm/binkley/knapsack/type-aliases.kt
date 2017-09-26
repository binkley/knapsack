package hm.binkley.knapsack

import kotlin.collections.MutableMap.MutableEntry

typealias Entry = MutableEntry<String, String?>

typealias Rule<T> = (String, DatabaseList) -> T
