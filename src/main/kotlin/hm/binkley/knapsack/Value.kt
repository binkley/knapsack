package hm.binkley.knapsack

import java.util.Objects

sealed class Value<V> {
    abstract var value: V

    class DatabaseValue(
            private val database: Database,
            val layer: Int,
            val key: String)
        : Value<String?>() {
        override var value: String?
            get() = database.selectOne(layer, key)
            set(newValue) = database.transaction {
                when (newValue) {
                    value -> Unit
                    null -> database.deleteOne(layer, key)
                    else -> database.upsertOne(layer, key, newValue)
                }
            }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DatabaseValue

            return layer == other.layer && key == other.key
        }

        override fun hashCode() = Objects.hash(layer, key)
    }

    class RuleValue<T>(override var value: Rule<T>) : Value<Rule<T>>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RuleValue<*>

            return value == other.value
        }

        override fun hashCode() = Objects.hash(value)
    }
}
