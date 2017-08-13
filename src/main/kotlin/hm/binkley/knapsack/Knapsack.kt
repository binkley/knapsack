package hm.binkley.knapsack

import kotlin.collections.MutableMap.MutableEntry

class Knapsack : AbstractMutableMap<String, Any>() {
    private val delegate: MutableMap<String, Any> = linkedMapOf()

    override fun put(key: String, value: Any): Any? = delegate.put(key, value)

    override val entries: MutableSet<MutableEntry<String, Any>>
        get() = delegate.entries
}
