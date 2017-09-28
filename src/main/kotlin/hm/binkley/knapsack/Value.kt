package hm.binkley.knapsack

sealed class Value {
    object NoValue : Value()
    data class StringValue(val value: String) : Value()
    data class RuleValue<T>(val rule: Rule<T>) : Value()
}
