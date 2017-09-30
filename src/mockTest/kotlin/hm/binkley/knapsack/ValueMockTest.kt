package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import hm.binkley.knapsack.Value.DatabaseValue
import org.junit.Test
import java.sql.Connection

internal class ValueMockTest {
    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val value = database.value(0, "foo")

    @Test
    fun shouldEquals() {
        assert.that(value, equalTo(database.value(value.layer, value.key)))
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
        assert.that(value == database.value(value.layer, value.key + "x"),
                equalTo(false))
    }

    @Test
    fun shouldNotEqualsByLayer() {
        assert.that(value == database.value(value.layer + 1, value.key),
                equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(value.hashCode() == database.value(value.layer,
                value.key).hashCode(), equalTo(true))
    }

    @Test
    fun shouldNotHashCodeByKey() {
        assert.that(value.hashCode() == database.value(value.layer,
                value.key + "x").hashCode(), equalTo(false))
    }

    @Test
    fun shouldNotHashCodeByLayer() {
        assert.that(value.hashCode() == database.value(value.layer + 1,
                value.key).hashCode(), equalTo(false))
    }

    @Test
    fun shouldGetValue() {
        doReturn("3").whenever(database).selectOne(value.layer, value.key)

        value.value

        verify(database).selectOne(value.layer, value.key)
    }

    @Test
    fun shouldGetNull() {
        doReturn(null).whenever(database).selectOne(value.layer, value.key)

        assert.that(value.value, absent())
    }

    @Test
    fun shouldSetValueFirstTime() {
        doNothing().whenever(database).upsertOne(value.layer, value.key, "3")
        doReturn(null, "3").
                whenever(database).selectOne(value.layer, value.key)

        value.value = "3"

        assert.that(value.value, equalTo("3"))

        verify(database, never()).deleteOne(any(), any())
        verify(database).upsertOne(value.layer, value.key, "3")
    }

    @Test
    fun shouldSetValueSecondTime() {
        doNothing().whenever(database).upsertOne(value.layer, value.key, "3")
        doReturn("2", "3").
                whenever(database).selectOne(value.layer, value.key)

        value.value = "3"

        assert.that(value.value, equalTo("3"))

        verify(database, never()).deleteOne(any(), any())
        verify(database).upsertOne(value.layer, value.key, "3")
    }

    @Test
    fun shouldSetNull() {
        doReturn("3", null).
                whenever(database).selectOne(value.layer, value.key)
        doNothing().whenever(database).deleteOne(value.layer, value.key)

        value.value = null

        assert.that(value.value, absent())

        verify(database).deleteOne(value.layer, value.key)
        verify(database, never()).upsertOne(any(), any(), any())
    }

    @Test
    fun shouldSetValueWithoutEffect() {
        doReturn("3", null).
                whenever(database).selectOne(value.layer, value.key)
        doNothing().whenever(database).upsertOne(value.layer, value.key, "3")

        value.value = "3"

        verify(database, never()).upsertOne(value.layer, value.key, "3")
    }

    @Test
    fun shouldTransact() {
        doReturn("3").whenever(database).selectOne(value.layer, value.key)
        doNothing().whenever(database).deleteOne(value.layer, value.key)

        value.value = null

        verify(database).transaction(any<() -> String?>())
    }
}
