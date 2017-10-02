package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import hm.binkley.knapsack.Value.DatabaseValue
import hm.binkley.knapsack.Value.NoValue
import hm.binkley.knapsack.Value.RuleValue
import org.junit.Test
import java.sql.Connection

internal class ValueMockTest {
    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val value = database.value(0, "foo", "3")

    @Test
    fun shouldEquals() {
        assert.that(value,
                equalTo(database.value(value.layer, value.key, value.value)))
    }

    @Test
    fun shouldEqualsReflexively() {
        assert.that(value == value, equalTo(true))
    }

    @Test
    fun shouldEqualsTrivially() {
        assert.that(value as DatabaseValue? == null, equalTo(false))
    }

    @Test
    fun shouldNotEqualsXenoxively() {
        assert.that(value as Any == this, equalTo(false))
    }

    @Test
    fun shouldNotEqualsByKey() {
        assert.that(value == database.value(value.layer, value.key + "x",
                value.value),
                equalTo(false))
    }

    @Test
    fun shouldNotEqualsByLayer() {
        assert.that(value == database.value(value.layer + 1, value.key,
                value.value),
                equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(value.hashCode() == database.value(value.layer,
                value.key, value.value).hashCode(), equalTo(true))
    }

    @Test
    fun shouldNotHashCodeByKey() {
        assert.that(value.hashCode() == database.value(value.layer,
                value.key + "x", value.value).hashCode(), equalTo(false))
    }

    @Test
    fun shouldNotHashCodeByLayer() {
        assert.that(value.hashCode() == database.value(value.layer + 1,
                value.key, value.value).hashCode(), equalTo(false))
    }

    @Test
    fun shouldGetValue() {
        value.value

        verifyZeroInteractions(database)
    }

    @Test
    fun shouldDereferenceDatabaseValueToNoValue() {
        doNothing().whenever(database).deleteOne(value.layer, value.key)

        value.dereference(NoValue)

        verify(database).deleteOne(value.layer, value.key)
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldDereferenceDatabaseValueToRuleValue() {
        doNothing().whenever(database).deleteOne(value.layer, value.key)

        value.dereference(RuleValue({ _, _ -> 3 }))

        verify(database).deleteOne(value.layer, value.key)
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldReferenceDatabaseValue() {
        doNothing().whenever(database).upsertOne(value.layer, value.key,
                value.value)

        value.reference()

        verify(database).upsertOne(value.layer, value.key, value.value)
        verifyNoMoreInteractions(database)
    }
}
