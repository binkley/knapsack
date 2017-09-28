package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Test

internal class ValueTest {
    @Test
    fun shouldMakeNoValue() {
        Value.NoValue
    }

    @Test
    fun shouldEqualsForStringValue() {
        assert.that(Value.StringValue("foo"),
                equalTo(Value.StringValue("foo")))
    }

    @Test
    fun shouldNotEqualsForStringValue() {
        assert.that(Value.StringValue("foo"),
                !equalTo(Value.StringValue("bar")))
    }

    @Test
    fun shouldHashCodeForStringValue() {
        assert.that(Value.StringValue("foo").hashCode(),
                equalTo(Value.StringValue("foo").hashCode()))
    }

    @Test
    fun shouldNotHashCodeForStringValue() {
        assert.that(Value.StringValue("foo").hashCode(),
                !equalTo(Value.StringValue("bar").hashCode()))
    }

    @Test
    fun shouldGetValueForStringValue() {
        assert.that(Value.StringValue("foo").value, equalTo("foo"))
    }

    @Test
    fun shouldSpreadForStringValue() {
        val (value) = Value.StringValue("foo")
        assert.that(value, equalTo("foo"))
    }

    @Test
    fun shouldCopyStringValue() {
        assert.that(Value.StringValue("foo").copy("bar").value,
                equalTo("bar"))
    }

    @Test
    fun shouldEqualsForRuleValue() {
        assert.that(Value.RuleValue(ruleA),
                equalTo(Value.RuleValue(ruleA)))
    }

    @Test
    fun shouldNotEqualsForRuleValue() {
        assert.that(Value.RuleValue(ruleA),
                !equalTo(Value.RuleValue(ruleB)))
    }

    @Test
    fun shouldHashCodeForRuleValue() {
        assert.that(Value.RuleValue(ruleA).hashCode(),
                equalTo(Value.RuleValue(ruleA).hashCode()))
    }

    @Test
    fun shouldNotHashCodeForRuleValue() {
        assert.that(Value.RuleValue(ruleA).hashCode(),
                !equalTo(Value.RuleValue(ruleB).hashCode()))
    }

    @Test
    fun shouldGetValueForRuleValue() {
        assert.that(Value.RuleValue(ruleA).rule, equalTo(ruleA))
    }

    @Test
    fun shouldSpreadForRuleValue() {
        val (value) = Value.RuleValue(ruleA)
        assert.that(value, equalTo(ruleA))
    }

    @Test
    fun shouldCopyRuleValue() {
        assert.that(Value.RuleValue(ruleA).copy(ruleB).rule,
                equalTo(ruleB))
    }

    companion object {
        private val ruleA: Rule<Int> = { _, _ -> 3 }
        private val ruleB: Rule<Int> = { _, _ -> 4 }
    }
}
