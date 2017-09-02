package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
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
internal class DatabaseEntryTest {
    @Rule
    @JvmField
    val thrown = ExpectedException.none()!!

    @Mock private lateinit var connection: Connection
    @Spy
    @InjectMocks private lateinit var database: Database
    private lateinit var entry: DatabaseEntry

    @Before
    fun setUpDatabase() {
        doReturn("3").`when`(database).selectOne(0, "foo")

        entry = DatabaseEntry("foo", database)
    }

    @Test
    fun shouldGetKey_forJaCoCo() {
        assert.that(entry.key, equalTo("foo"))
    }

    @Test
    fun shouldEquals() {
        assert.that(entry, equalTo(DatabaseEntry("foo", database)))
    }

    @Suppress("ReplaceCallWithComparison")
    @Test
    fun shouldNotEquals() {
        assert.that(entry.equals(this), equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode(),
                equalTo(DatabaseEntry("foo", database).hashCode()))
    }

    @Test
    fun shouldGetValue() {
        entry.value

        verify(database).selectOne(0, "foo")
    }

    @Test
    fun shouldGetNull() {
        doReturn(null).`when`(database).selectOne(0, "foo")

        assert.that(entry.value, absent())
    }

    @Test
    fun shouldSetValueFirstTime() {
        doNothing().`when`(database).upsertOne(0, "foo", "3")
        doReturn(null, "3").`when`(database).selectOne(0, "foo")

        val previous = entry.setValue("3")

        assert.that(previous, absent())
        assert.that(entry.value, equalTo("3"))

        verify(database, never()).deleteOne(eq(0), anyString())
        verify(database).upsertOne(0, "foo", "3")
    }

    @Test
    fun shouldSetValueSecondTime() {
        doNothing().`when`(database).upsertOne(0, "foo", "3")
        doReturn("2", "3").`when`(database).selectOne(0, "foo")

        val previous = entry.setValue("3")

        assert.that(previous, equalTo("2"))
        assert.that(entry.value, equalTo("3"))

        verify(database, never()).deleteOne(eq(0), anyString())
        verify(database).upsertOne(0, "foo", "3")
    }

    @Test
    fun shouldSetNull() {
        doReturn("3", null).`when`(database).selectOne(0, "foo")
        doNothing().`when`(database).deleteOne(0, "foo")

        val previous = entry.setValue(null)

        assert.that(previous, equalTo("3"))
        assert.that(entry.value, absent())

        verify(database).deleteOne(0, "foo")
        verify(database, never()).upsertOne(eq(0), anyString(), anyString())
    }

    @Test
    fun shouldCommit() {
        doNothing().`when`(database).deleteOne(0, "foo")

        entry.setValue(null)

        verify(connection).commit()
    }

    @Test
    fun shouldRollback() {
        thrown.expect(SQLException::class.java)
        doThrow(SQLException::class.java).`when`(database).deleteOne(0, "foo")

        entry.setValue(null)

        verify(connection).rollback()
    }
}
