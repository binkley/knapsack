package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import hm.binkley.knapsack.Value.IntValue
import hm.binkley.knapsack.Value.StringValue
import org.junit.Test

class ValueTest {
    @Test
    fun shouldReadString() {
        assert.that(StringValue.read("string"), equalTo("string"))
    }

    @Test
    fun shouldWriteString() {
        assert.that(StringValue.write("string"), equalTo("string"))
    }

    @Test
    fun shouldReadInt() {
        assert.that(IntValue.read("1"), equalTo(1))
    }

    @Test
    fun shouldWriteInt() {
        assert.that(IntValue.write(1), equalTo("1"))
    }
}
