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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseEntryMockTest {
    @Rule
    @JvmField
    val thrown = ExpectedException.none()!!

    private val connection: Connection = mock()
    private val database: Database = spy(Database(connection))
    private val entry = database.entry(0, "foo")

    @Before
    fun setUp() {
        doReturn("3").whenever(database).selectOne(entry.layer, entry.key)
    }

    @Test
    fun shouldEquals() {
        assert.that(entry, equalTo(database.entry(entry.layer, entry.key)))
    }

    @Suppress("ReplaceCallWithComparison")
    @Test
    fun shouldNotEquals() {
        assert.that(entry == database.entry(entry.layer + 1, entry.key),
                equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode() == database.entry(entry.layer,
                entry.key).hashCode(), equalTo(true))
    }

    @Test
    fun shouldNotHashCode() {
        assert.that(entry.hashCode() == database.entry(entry.layer + 1,
                entry.key).hashCode(), equalTo(false))
    }

    @Test
    fun shouldGetValue() {
        entry.value

        verify(database).selectOne(entry.layer, entry.key)
    }

    @Test
    fun shouldGetNull() {
        doReturn(null).whenever(database).selectOne(entry.layer, entry.key)

        assert.that(entry.value, absent())
    }

    @Test
    fun shouldSetValueFirstTime() {
        doNothing().whenever(database).upsertOne(entry.layer, entry.key, "3")
        doReturn(null, "3").
                whenever(database).selectOne(entry.layer, entry.key)

        val previous = entry.setValue("3")

        assert.that(previous, absent())
        assert.that(entry.value, equalTo("3"))

        verify(database, never()).deleteOne(any(), any())
        verify(database).upsertOne(entry.layer, entry.key, "3")
    }

    @Test
    fun shouldSetValueSecondTime() {
        doNothing().whenever(database).upsertOne(entry.layer, entry.key, "3")
        doReturn("2", "3").
                whenever(database).selectOne(entry.layer, entry.key)

        val previous = entry.setValue("3")

        assert.that(previous, equalTo("2"))
        assert.that(entry.value, equalTo("3"))

        verify(database, never()).deleteOne(any(), any())
        verify(database).upsertOne(entry.layer, entry.key, "3")
    }

    @Test
    fun shouldSetNull() {
        doReturn("3", null).
                whenever(database).selectOne(entry.layer, entry.key)
        doNothing().whenever(database).deleteOne(entry.layer, entry.key)

        val previous = entry.setValue(null)

        assert.that(previous, equalTo("3"))
        assert.that(entry.value, absent())

        verify(database).deleteOne(entry.layer, entry.key)
        verify(database, never()).upsertOne(any(), any(), any())
    }

    @Test
    fun shouldTransact() {
        doNothing().whenever(database).deleteOne(entry.layer, entry.key)

        entry.setValue(null)

        verify(database).transaction(any<() -> String?>())
    }
}
