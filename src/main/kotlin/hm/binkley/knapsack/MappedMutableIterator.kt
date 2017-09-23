package hm.binkley.knapsack

@Suppress("UNCHECKED_CAST")
open class MappedMutableIterator<T, out R>(
        private val iter: MutableIterator<T>,
        private val transform: (T) -> R,
        private val deleter: (R) -> Unit)
    : MutableIterator<R> {
    private var value: R? = null

    override fun hasNext() = iter.hasNext()

    override fun next(): R {
        value = transform(iter.next())
        return value as R
    }

    override fun remove() {
        iter.remove()
        deleter(value as R)
    }
}
