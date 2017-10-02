package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import hm.binkley.knapsack.Value.NoValue
import hm.binkley.knapsack.Value.RuleValue
import org.junit.Before
import org.junit.Test

internal class ValueTest {
    private lateinit var value: RuleValue<Int>

    @Before
    fun setUp() {
        value = RuleValue(ruleA)
    }

    @Test
    fun shouldEqualsReflexively() {
        assert.that(value == value, equalTo(true))
    }

    @Test
    fun shouldEqualsTrivially() {
        assert.that(value as RuleValue? == null, equalTo(false))
    }

    @Test
    fun shouldNotEqualsXenoxively() {
        assert.that(value as Any == this, equalTo(false))
    }

    @Test
    fun shouldNotEqualsByRule() {
        assert.that(value == RuleValue(ruleB), equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(value.hashCode() == RuleValue(ruleA).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeByRule() {
        assert.that(value.hashCode() == RuleValue(ruleB).hashCode(),
                equalTo(false))
    }

    @Test
    fun shouldGetRule() {
        assert.that(value.rule, equalTo(ruleA))
    }

    @Test
    fun shouldDereferenceNoValueToNoValue() {
        NoValue.dereference(NoValue)
    }

    @Test
    fun shouldDereferenceNoValueToRuleValue() {
        NoValue.dereference(RuleValue(ruleB))
    }

    @Test
    fun shouldDereferenceRuleValueToNoValue() {
        value.dereference(NoValue)
    }

    @Test
    fun shouldDereferenceRuleValueToRuleValue() {
        value.dereference(RuleValue(ruleB))
    }

    @Test
    fun shouldReferenceNoValue() {
        NoValue.reference()
    }

    @Test
    fun shouldReferenceRuleValue() {
        value.reference()
    }

    companion object {
        private val ruleA: Rule<Int> = { _, _ -> 3 }
        private val ruleB: Rule<Int> = { _, _ -> 4 }
    }
}
