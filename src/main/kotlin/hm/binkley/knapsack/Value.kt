package hm.binkley.knapsack

import java.util.Objects

sealed class Value {
    open fun dereference(newValue: Value) = Unit
    open fun reference() = Unit

    object NoValue : Value()

    class DatabaseValue(
            private val database: Database,
            val layer: Int,
            val key: String,
            val value: String)
        : Value() {
        override fun dereference(newValue: Value) {
            if (newValue !is DatabaseValue)
                database.deleteOne(layer, key)
        }

        override fun reference() {
            database.upsertOne(layer, key, value)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DatabaseValue

            return layer == other.layer && key == other.key
                    && value == other.value
        }

        override fun hashCode() = Objects.hash(layer, key, value)
    }

    class RuleValue<out T>(val rule: Rule<T>) : Value() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RuleValue<*>

            return rule == other.rule
        }

        override fun hashCode() = Objects.hash(rule)
    }
}
