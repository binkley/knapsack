package hm.binkley.knapsack

fun <T, R> MutableIterator<T>.map(transform: (T) -> R, deleter: (R) -> Unit)
        = MappedMutableIterator(this, transform, deleter)

fun iteratorOf() = listOf<String>().iterator()
fun iteratorOf(item: String) = listOf(item).iterator()
fun mutableIteratorOf(item: String) = mutableListOf(item).iterator()
