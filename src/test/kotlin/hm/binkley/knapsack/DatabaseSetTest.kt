package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseSetTest {
    @Mock private lateinit var connection: Connection
    @Spy
    @InjectMocks private lateinit var database: Database
    @Mock private lateinit var selectAllResults: ResultSet
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        doReturn(selectAllResults).`when`(database).selectAll()

        set = DatabaseSet(database)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).`when`(database).countAll()

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectAllResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).`when`(database).countAll()

        assert.that(set, equalTo(DatabaseSet(database)))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        assert.that(set.hashCode(), equalTo(DatabaseSet(database).hashCode()))
    }

    @Test
    fun shouldFindEntry() {
        `when`(selectAllResults.next()).thenReturn(true, false)
        `when`(selectAllResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(DatabaseEntry("foo", database)),
                equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doNothing().`when`(database).deleteOne("foo")
        doReturn("3").`when`(database).selectOne("foo")
        `when`(selectAllResults.next()).thenReturn(true, false)
        `when`(selectAllResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(DatabaseEntry("foo", database)),
                equalTo(true))

        verify(database).deleteOne("foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().`when`(database).deleteOne("foo")
        doReturn(null, "3").`when`(database).selectOne("foo")

        val changed = set.add(DatabaseEntry("foo", database))

        assert.that(changed, equalTo(true))
    }
}
