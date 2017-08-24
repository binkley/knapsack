package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import hm.binkley.knapsack.DatabaseEntry.Companion.VALUE_COLUMN
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.eq
import org.mockito.junit.MockitoJUnitRunner
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseEntryTest {
    @Mock private lateinit var selectOne: PreparedStatement
    @Mock private lateinit var results: ResultSet
    @Mock private lateinit var upsertOne: PreparedStatement
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var entry: DatabaseEntry

    @Before
    fun setUpDatabase() {
        `when`(selectOne.executeQuery()).thenReturn(results)

        entry = DatabaseEntry("foo", selectOne, upsertOne, deleteOne)
    }

    @Test
    fun shouldGetKey_forJaCoCo() {
        assert.that(entry.key, equalTo("foo"))
    }

    @Test
    fun shouldEquals() {
        assert.that(entry, equalTo(DatabaseEntry("foo", selectOne, upsertOne,
                deleteOne)))
    }

    @Suppress("ReplaceCallWithComparison")
    @Test
    fun shouldNotEquals() {
        assert.that(entry.equals(this), equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode(),
                equalTo(DatabaseEntry("foo", selectOne, upsertOne,
                        deleteOne).hashCode()))
    }

    @Test
    fun shouldGetValue() {
        `when`(results.next()).thenReturn(true, false)
        `when`(results.getString(eq(VALUE_COLUMN))).thenReturn("3")

        assert.that(entry.value, equalTo("3"))
    }

    @Test
    fun shouldGetNull() {
        `when`(results.next()).thenReturn(false)

        assert.that(entry.value, absent())
    }

    @Test(expected = IllegalStateException::class)
    fun shouldGetAnExceptionWhenMultipleValuesMatchKey() {
        `when`(results.next()).thenReturn(true, true)

        entry.value
    }

    @Test
    fun shouldSetValueFirstTime() {
        `when`(results.next()).thenReturn(false, true, false)
        `when`(results.getString(eq(VALUE_COLUMN))).thenReturn("3")

        val previous = entry.setValue("3")

        assert.that(previous, absent())
        assert.that(entry.value, equalTo("3"))
    }

    @Test
    fun shouldSetValueSecondTime() {
        `when`(results.next()).thenReturn(true, false, true, false)
        `when`(results.getString(eq(VALUE_COLUMN))).thenReturn("2", "3")

        val previous = entry.setValue("3")

        assert.that(previous, equalTo("2"))
        assert.that(entry.value, equalTo("3"))
    }

    @Test
    fun shouldSetNull() {
        `when`(results.next()).thenReturn(true, false, false)
        `when`(results.getString(eq(VALUE_COLUMN))).thenReturn("3")

        val previous = entry.setValue(null)

        assert.that(previous, equalTo("3"))
        assert.that(entry.value, absent())
    }
}
