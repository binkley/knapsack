package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import hm.binkley.knapsack.Value.RuleValue
import hm.binkley.knapsack.Value.StringValue
import org.junit.Test

internal class ValueTest {
    @Test
    fun shouldEqualsForStringValue() {
        assert.that(StringValue("foo"), equalTo(StringValue("foo")))
    }

    @Test
    fun shouldNotEqualsForStringValue() {
        assert.that(StringValue("foo"), !equalTo(StringValue("bar")))
    }

    @Test
    fun shouldHashCodeForStringValue() {
        assert.that(StringValue("foo").hashCode(),
                equalTo(StringValue("foo").hashCode()))
    }

    @Test
    fun shouldNotHashCodeForStringValue() {
        assert.that(StringValue("foo").hashCode(),
                !equalTo(StringValue("bar").hashCode()))
    }

    @Test
    fun shouldGetValueForStringValue() {
        assert.that(StringValue("foo").value, equalTo("foo"))
    }

    @Test
    fun shouldSpreadForStringValue() {
        val (value) = StringValue("foo")
        assert.that(value, equalTo("foo"))
    }

    @Test
    fun shouldCopyStringValue() {
        assert.that(StringValue("foo").copy("bar").value,
                equalTo("bar"))
    }

    @Test
    fun shouldEqualsForRuleValue() {
        assert.that(RuleValue(ruleA), equalTo(RuleValue(ruleA)))
    }

    @Test
    fun shouldNotEqualsForRuleValue() {
        assert.that(RuleValue(ruleA), !equalTo(RuleValue(ruleB)))
    }

    @Test
    fun shouldHashCodeForRuleValue() {
        assert.that(RuleValue(ruleA).hashCode(),
                equalTo(RuleValue(ruleA).hashCode()))
    }

    @Test
    fun shouldNotHashCodeForRuleValue() {
        assert.that(RuleValue(ruleA).hashCode(),
                !equalTo(RuleValue(ruleB).hashCode()))
    }

    @Test
    fun shouldGetValueForRuleValue() {
        assert.that(RuleValue(ruleA).rule, equalTo(ruleA))
    }

    @Test
    fun shouldSpreadForRuleValue() {
        val (value) = RuleValue(ruleA)

        assert.that(value, equalTo(ruleA))
    }

    @Test
    fun shouldCopyRuleValue() {
        assert.that(RuleValue(ruleA).copy(ruleB).rule, equalTo(ruleB))
    }

    companion object {
        private val ruleA: Rule<Int> = { _, _ -> 3 }
        private val ruleB: Rule<Int> = { _, _ -> 4 }
    }
}
