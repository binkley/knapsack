package hm.binkley.knapsack

fun <T, R> MutableIterator<T>.map(transform: (T) -> R, deleter: (R) -> Unit)
        = MappedMutableIterator(this, transform, deleter)
