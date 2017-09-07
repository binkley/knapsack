package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.SQLException

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseEntryMockTest {
    @Rule
    @JvmField
    val thrown = ExpectedException.none()!!

    @Mock private lateinit var connection: Connection
    @Spy
    @InjectMocks private lateinit var database: Database
    private lateinit var entry: DatabaseEntry

    @Before
    fun setUpDatabase() {
        entry = database.entry(0, "foo")

        doReturn("3").`when`(database).selectOne(entry.layer, entry.key)
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
        doReturn(null).`when`(database).selectOne(entry.layer, entry.key)

        assert.that(entry.value, absent())
    }

    @Test
    fun shouldSetValueFirstTime() {
        doNothing().`when`(database).upsertOne(entry.layer, entry.key, "3")
        doReturn(null, "3").`when`(database).selectOne(entry.layer, entry.key)

        val previous = entry.setValue("3")

        assert.that(previous, absent())
        assert.that(entry.value, equalTo("3"))

        verify(database, never()).deleteOne(anyInt(), anyString())
        verify(database).upsertOne(entry.layer, entry.key, "3")
    }

    @Test
    fun shouldSetValueSecondTime() {
        doNothing().`when`(database).upsertOne(entry.layer, entry.key, "3")
        doReturn("2", "3").`when`(database).selectOne(entry.layer, entry.key)

        val previous = entry.setValue("3")

        assert.that(previous, equalTo("2"))
        assert.that(entry.value, equalTo("3"))

        verify(database, never()).deleteOne(anyInt(), anyString())
        verify(database).upsertOne(entry.layer, entry.key, "3")
    }

    @Test
    fun shouldSetNull() {
        doReturn("3", null).`when`(database).selectOne(entry.layer, entry.key)
        doNothing().`when`(database).deleteOne(entry.layer, entry.key)

        val previous = entry.setValue(null)

        assert.that(previous, equalTo("3"))
        assert.that(entry.value, absent())

        verify(database).deleteOne(entry.layer, entry.key)
        verify(database, never()).upsertOne(anyInt(), anyString(), anyString())
    }

    @Test
    fun shouldCommit() {
        doNothing().`when`(database).deleteOne(entry.layer, entry.key)

        entry.setValue(null)

        verify(connection).commit()
    }

    @Test
    fun shouldRollback() {
        thrown.expect(SQLException::class.java)
        doThrow(SQLException::class.java).`when`(database).deleteOne(
                entry.layer, entry.key)

        entry.setValue(null)

        verify(connection).rollback()
    }
}
