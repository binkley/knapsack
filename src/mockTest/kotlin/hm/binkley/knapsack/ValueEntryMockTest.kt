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
import org.junit.Test
import java.sql.Connection

class ValueEntryMockTest {
    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val entry = database.valueEntry(0, "foo")

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

        verifyZeroInteractions(database)
    }

    @Test
    fun shouldNoValueToDatabaseValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")

        entry.setValue("3")

        verify(database).upsertOne(0, "foo", "3")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldNoValueToRuleValue() {
        entry.setValue({ _, _ -> 3 })

        verifyZeroInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToNoValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)
        doNothing().whenever(database).deleteOne(0, "foo")

        entry.setValue(null)

        verify(database).deleteOne(0, "foo")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToDatabaseValueWithSaveValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)

        entry.setValue("3")

        verifyZeroInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToDatabaseValueWithDifferentValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)
        doNothing().whenever(database).upsertOne(0, "foo", "4")

        entry.setValue("4")

        verify(database).upsertOne(0, "foo", "4")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldDatabaseValueToRuleValue() {
        doNothing().whenever(database).upsertOne(0, "foo", "3")
        entry.setValue("3")
        reset(database)
        doNothing().whenever(database).deleteOne(0, "foo")

        entry.setValue({ _, _ -> 4 })

        verify(database).deleteOne(0, "foo")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldRuleValueToNoValue() {
        entry.setValue({ _, _ -> 3 })

        entry.setValue(null)

        verifyZeroInteractions(database)
    }

    @Test
    fun shouldRuleValueToDatabaseValue() {
        entry.setValue({ _, _ -> 3 })
        doNothing().whenever(database).upsertOne(0, "foo", "3")

        entry.setValue("3")

        verify(database).upsertOne(0, "foo", "3")
        verifyNoMoreInteractions(database)
    }

    @Test
    fun shouldRuleValueToRuleValue() {
        entry.setValue({ _, _ -> 3 })

        entry.setValue({ _, _ -> 4 })

        verifyZeroInteractions(database)
    }

    @Test
    fun shouldEquals() {
        assert.that(entry, equalTo(database.valueEntry(entry.layer, entry.key)))
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
        assert.that(entry == database.valueEntry(entry.layer, entry.key + "x"),
                equalTo(false))
    }

    @Test
    fun shouldNotEqualsByLayer() {
        assert.that(entry == database.valueEntry(entry.layer + 1, entry.key),
                equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode() == database.valueEntry(entry.layer,
                entry.key).hashCode(), equalTo(true))
    }

    @Test
    fun shouldNotHashCodeByKey() {
        assert.that(entry.hashCode() == database.valueEntry(entry.layer,
                entry.key + "x").hashCode(), equalTo(false))
    }

    @Test
    fun shouldNotHashCodeByLayer() {
        assert.that(entry.hashCode() == database.valueEntry(entry.layer + 1,
                entry.key).hashCode(), equalTo(false))
    }
}
