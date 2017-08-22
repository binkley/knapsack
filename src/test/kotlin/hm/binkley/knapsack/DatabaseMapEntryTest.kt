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
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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

        verify(select, times(1)).setString(1, "foo")
        verify(select, times(1)).executeQuery()
        verify(insert, never()).executeUpdate()
        verify(delete, never()).executeUpdate()
    }

    @Test
    fun shouldGetNull() {
        `when`(results.next()).thenReturn(false)

        assert.that(entry.value, absent())

        verify(select, times(1)).setString(1, "foo")
        verify(select, times(1)).executeQuery()
        verify(insert, never()).executeUpdate()
        verify(delete, never()).executeUpdate()
    }

    @Test
    fun shouldSetValue() {
        entry.value = "3"

        verify(insert, times(1)).setString(1, "foo")
        verify(insert, times(1)).setString(2, "3")
        verify(insert, times(1)).executeUpdate()
        verify(select, never()).executeUpdate()
        verify(delete, never()).executeUpdate()
    }

    @Test
    fun shouldSetNull() {
        entry.value = null

        verify(delete, times(1)).setString(1, "foo")
        verify(delete, times(1)).executeUpdate()
        verify(select, never()).executeUpdate()
        verify(insert, never()).executeUpdate()
    }
}
