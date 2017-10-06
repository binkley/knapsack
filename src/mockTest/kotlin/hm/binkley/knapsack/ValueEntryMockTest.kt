package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import hm.binkley.knapsack.Value.NoValue
import hm.binkley.knapsack.Value.RuleValue
import org.junit.Test
import java.sql.Connection

class ValueEntryMockTest {
    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val entry = database.entry(0, "foo", NoValue)

    @Test
    fun shouldGetLayer() {
        assert.that(entry.layer, equalTo(0))
    }

    @Test
    fun shouldGetKey() {
        assert.that(entry.key, equalTo("foo"))
    }

    @Test
    fun shouldGetValue() {
        assert.that(entry.value is NoValue, equalTo(true))
    }

    @Test
    fun shouldNoValueToNoValue() {
        entry.setValue(null)

        assert.that(entry.value == NoValue, equalTo(true))
        verifyZeroInteractions(database)
    }

    @Test
    fun shouldNoValueToDatabaseValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")

        entry.setValue("3")

        assert.that(entry.value == database.value(0, "foo", "3"),
                equalTo(true))
        verify(database).upsertOne(0, "foo", "3")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldNoValueToRuleValue() {
        val rule: Rule<Int> = { _, _ -> 3 }
        entry.setValue(rule)

        assert.that(entry.value == RuleValue(rule), equalTo(true))
        verifyZeroInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToNoValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)
        doNothing().whenever(database).deleteOne(0, "foo")

        entry.setValue(null)

        assert.that(entry.value == NoValue, equalTo(true))
        verify(database).deleteOne(0, "foo")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToDatabaseValueWithSaveValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)

        entry.setValue("3")

        assert.that(entry.value == database.value(0, "foo", "3"),
                equalTo(true))
        verifyZeroInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToDatabaseValueWithDifferentValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)
        doNothing().whenever(database).upsertOne(0, "foo", "4")

        entry.setValue("4")

        assert.that(entry.value == database.value(0, "foo", "4"),
                equalTo(true))
        verify(database).upsertOne(0, "foo", "4")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToRuleValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)
        doNothing().whenever(database).deleteOne(0, "foo")

        val rule: Rule<Int> = { _, _ -> 4 }
        entry.setValue(rule)

        assert.that(entry.value == RuleValue(rule), equalTo(true))
        verify(database).deleteOne(0, "foo")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldRuleValueToNoValue() {
        entry.setValue({ _, _ -> 3 })

        entry.setValue(null)

        assert.that(entry.value == NoValue, equalTo(true))
        verifyZeroInteractions(database)
    }

    @Test
    fun shouldRuleValueToDatabaseValue() {
        entry.setValue({ _, _ -> 3 })
        doNothing().whenever(database).upsertOne(0, "foo", "3")

        entry.setValue("3")

        assert.that(entry.value == database.value(0, "foo", "3"),
                equalTo(true))
        verify(database).upsertOne(0, "foo", "3")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldRuleValueToRuleValue() {
        entry.setValue({ _, _ -> 3 })

        val rule: Rule<Int> = { _, _ -> 4 }
        entry.setValue(rule)

        assert.that(entry.value == RuleValue(rule), equalTo(true))
        verifyZeroInteractions(database)
    }

    @Test
    fun shouldEquals() {
        assert.that(entry,
                equalTo(database.entry(entry.layer, entry.key, entry.value)))
    }

    @Test
    fun shouldEqualsReflexively() {
        assert.that(entry == entry, equalTo(true))
    }

    @Test
    fun shouldEqualsTrivially() {
        assert.that(entry as ValueEntry? == null, equalTo(false))
    }

    @Test
    fun shouldNotEqualsXenoxively() {
        assert.that(entry as Any == this, equalTo(false))
    }

    @Test
    fun shouldNotEqualsByKey() {
        assert.that(entry == database.entry(entry.layer, entry.key + "x",
                entry.value),
                equalTo(false))
    }

    @Test
    fun shouldNotEqualsByLayer() {
        assert.that(entry == database.entry(entry.layer + 1, entry.key,
                entry.value),
                equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode() == database.entry(entry.layer,
                entry.key, entry.value).hashCode(), equalTo(true))
    }

    @Test
    fun shouldNotHashCodeByKey() {
        assert.that(entry.hashCode() == database.entry(entry.layer,
                entry.key + "x", entry.value).hashCode(), equalTo(false))
    }

    @Test
    fun shouldNotHashCodeByLayer() {
        assert.that(entry.hashCode() == database.entry(entry.layer + 1,
                entry.key, entry.value).hashCode(), equalTo(false))
    }
}
