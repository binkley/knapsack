package hm.binkley.knapsack

import hm.binkley.knapsack.Value.DatabaseValue
import hm.binkley.knapsack.Value.NoValue
import hm.binkley.knapsack.Value.RuleValue
import java.util.Objects

class DatabaseMap(private val database: Database, val layer: Int)
    : AbstractMutableMap<String, Value>() {
    init {
        if (0 > layer) throw IndexOutOfBoundsException("Layer: $layer")
        if (database.countList() < layer)
            throw IndexOutOfBoundsException("Layer: $layer")
    }

    override val entries: MutableSet<Entry> = database.set(layer)

    fun containsValue(value: String)
            = entries.
            filter { it.value is DatabaseValue }.
            map { it.value as DatabaseValue }.
            filter { it.value == value }.
            any()

    fun <T> containsRule(rule: Rule<T>)
            = entries.
            filter { it.value is RuleValue<*> }.
            map { it.value as RuleValue<*> }.
            filter { it.rule == rule }.
            any()

    override fun get(key: String) = super.get(key) ?: NoValue

    override fun getOrDefault(key: String, defaultValue: Value): Value {
        val value = super.getOrDefault(key, defaultValue)
        return if (NoValue == value) defaultValue else value
    }

    fun getOrDefault(key: String, value: String)
            = getOrDefault(key, database.value(layer, key, value))

    fun <T> getOrDefault(key: String, rule: Rule<T>)
            = getOrDefault(key, RuleValue(rule))

    override fun put(key: String, value: Value)
            = database.entry(layer, key).setValue(value)

    operator fun set(key: String, nonce: Nothing?) = put(key, NoValue)

    operator fun set(key: String, value: String)
            = put(key, database.value(layer, key, value))

    operator fun <T> set(key: String, rule: Rule<T>)
            = put(key, RuleValue(rule))

    override fun remove(key: String): Value {
        val oldValue = get(key)
        oldValue.dereference(NoValue)
        return oldValue
    }

    override fun remove(key: String, value: Value): Boolean {
        val oldValue = get(key)
        if (oldValue == value) {
            oldValue.dereference(NoValue)
            return true
        }
        return false
    }

    fun remove(key: String, value: String)
            = remove(key, database.value(layer, key, value))

    fun <T> remove(key: String, rule: Rule<T>) = remove(key, RuleValue(rule))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DatabaseMap

        return layer == other.layer && super.equals(other)
    }

    override fun hashCode() = Objects.hash(layer)
}
