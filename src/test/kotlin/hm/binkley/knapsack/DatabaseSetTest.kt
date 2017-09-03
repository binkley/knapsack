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
    @Mock private lateinit var otherSelectKeysResults: ResultSet
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        val layer = 0
        doReturn(selectKeysResults).`when`(database).selectKeys(layer)
        doReturn(otherSelectKeysResults).`when`(database).selectKeys(
                layer + 1)
        set = newDatabaseSet(layer)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).`when`(database).countAll(set.layer)

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectKeysResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).`when`(database).countAll(set.layer)

        assert.that(set == newDatabaseSet(set.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(set == newDatabaseSet(set.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        `when`(selectKeysResults.next()).thenReturn(false, false)

        assert.that(set.hashCode() == newDatabaseSet(set.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        `when`(selectKeysResults.next()).thenReturn(false)
        `when`(otherSelectKeysResults.next()).thenReturn(false)

        assert.that(
                set.hashCode() == newDatabaseSet(set.layer + 1).hashCode(),
                equalTo(false))
    }

    @Test
    fun shouldFindEntry() {
        `when`(selectKeysResults.next()).thenReturn(true, false)
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(newDatabaseEntry("foo")), equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doNothing().`when`(database).deleteOne(set.layer, "foo")
        doReturn("3").`when`(database).selectOne(set.layer, "foo")
        `when`(selectKeysResults.next()).thenReturn(true, false)
        `when`(selectKeysResults.row).thenReturn(1)
        `when`(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(newDatabaseEntry("foo")), equalTo(true))

        verify(database).deleteOne(set.layer, "foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().`when`(database).deleteOne(set.layer, "foo")
        doReturn(null, "3").`when`(database).selectOne(set.layer, "foo")

        val changed = set.add(newDatabaseEntry("foo"))

        assert.that(changed, equalTo(true))
    }

    private fun newDatabaseEntry(key: String)
            = DatabaseEntry(set.layer, key, database)

    private fun newDatabaseSet(layer: Int)
            = DatabaseSet(layer, database)
}
