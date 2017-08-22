package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
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
class DatabaseMapEntryTest {
    @Mock private lateinit var select: PreparedStatement
    @Mock private lateinit var results: ResultSet
    @Mock private lateinit var insert: PreparedStatement
    @Mock private lateinit var delete: PreparedStatement
    private lateinit var entry: DatabaseMapEntry

    @Before
    fun setUpDatase() {
        `when`(select.executeQuery()).thenReturn(results)

        entry = DatabaseMapEntry("foo", select, insert, delete)
    }

    @Test
    fun shouldGetKey_forJaCoCo() {
        assert.that(entry.key, equalTo("foo"))
    }

    @Test
    fun shouldEquals() {
        assert.that(entry, equalTo(DatabaseMapEntry("foo", select, insert,
                delete)))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode(),
                equalTo(DatabaseMapEntry("foo", select, insert,
                        delete).hashCode()))
    }

    @Test
    fun shouldGetValue() {
        `when`(results.next()).thenReturn(true, false)
        `when`(results.getString(eq("value"))).thenReturn("3")

        assert.that(entry.value, equalTo("3"))
    }

    @Test
    fun shouldGetNull() {
        `when`(results.next()).thenReturn(false)

        assert.that(entry.value, absent())
    }

    @Test
    fun shouldSetValueFirstTime() {
        val previous = entry.setValue("3")

        assert.that(previous, absent())
    }

    @Test
    fun shouldSetValueSecondTime() {
        `when`(results.next()).thenReturn(true, false)
        `when`(results.getString(eq("value"))).thenReturn("3")

        val previous = entry.setValue("4")

        assert.that(previous, equalTo("3"))
    }

    @Test
    fun shouldSetNull() {
        entry.setValue(null)

        assert.that(entry.value, absent())
    }
}
