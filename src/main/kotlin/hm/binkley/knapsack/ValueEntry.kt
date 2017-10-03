package hm.binkley.knapsack

import hm.binkley.knapsack.Value.DatabaseValue
import hm.binkley.knapsack.Value.NoValue
import hm.binkley.knapsack.Value.RuleValue
import java.util.Objects
import kotlin.collections.MutableMap.MutableEntry

class ValueEntry(
        private val database: Database,
        val layer: Int,
        override val key: String)
    : MutableEntry<String, Value> {
    private var v: Value = NoValue

    override val value: Value
        get() = v

    override fun setValue(newValue: Value): Value {
        val previous = v
        if (previous == newValue)
            return previous
        previous.dereference(newValue)
        v = newValue
        newValue.reference()
        return previous
    }

    fun setValue(nonce: Nothing?) = setValue(NoValue)

    fun setValue(newValue: String)
            = setValue(DatabaseValue(database, layer, key, newValue))

    fun <T> setValue(rule: Rule<T>) = setValue(RuleValue(rule) as Value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueEntry

        return layer == other.layer && key == other.key
    }

    override fun hashCode() = Objects.hash(layer, key)
}
