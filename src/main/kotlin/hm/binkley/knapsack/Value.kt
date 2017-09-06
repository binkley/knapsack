package hm.binkley.knapsack

sealed class Value<T : Any>(
        val write: (T) -> String,
        val read: (String?) -> T?) {
    object StringValue : Value<String>({ it }, { it })
    object IntValue : Value<Int>({ it.toString() }, { it?.toInt() })
}
