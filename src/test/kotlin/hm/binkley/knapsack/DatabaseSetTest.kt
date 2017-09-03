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
    @Mock private lateinit var selectKeysResults: ResultSet
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        doReturn(selectKeysResults).`when`(database).selectKeys(0)

        set = newDatabaseSet()
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).`when`(database).countAll(0)

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectKeysResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).`when`(database).countAll(0)

        assert.that(set, equalTo(newDatabaseSet()))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        assert.that(set.hashCode(), equalTo(newDatabaseSet().hashCode()))
    }

    @Test
    fun shouldFindEntry() {
        `when`(selectKeysResults.next()).thenReturn(true, false)
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(newDatabaseEntry("foo")), equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doNothing().`when`(database).deleteOne(0, "foo")
        doReturn("3").`when`(database).selectOne(0, "foo")
        `when`(selectKeysResults.next()).thenReturn(true, false)
        `when`(selectKeysResults.row).thenReturn(1)
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(newDatabaseEntry("foo")), equalTo(true))

        verify(database).deleteOne(0, "foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().`when`(database).deleteOne(0, "foo")
        doReturn(null, "3").`when`(database).selectOne(0, "foo")

        val changed = set.add(newDatabaseEntry("foo"))

        assert.that(changed, equalTo(true))
    }

    private fun newDatabaseEntry(key: String)
            = DatabaseEntry(set.layer, key, database)

    private fun newDatabaseSet()
            = DatabaseSet(0, database)
}
