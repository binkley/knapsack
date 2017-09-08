package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseSetMockTest {
    @Mock private lateinit var connection: Connection
    @Spy
    @InjectMocks private lateinit var database: Database
    @Mock private lateinit var selectKeysResults: ResultSet
    @Mock private lateinit var otherSelectKeysResults: ResultSet
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        val layer = 0
        doReturn(selectKeysResults).whenever(database).selectMapKeys(layer)
        doReturn(otherSelectKeysResults).whenever(database).selectMapKeys(
                layer + 1)
        set = database.set(layer)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countMap(set.layer)

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        whenever(selectKeysResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).whenever(database).countMap(set.layer)

        assert.that(set == database.set(set.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(set == database.set(set.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false, false)

        assert.that(set.hashCode() == database.set(set.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false)
        whenever(otherSelectKeysResults.next()).thenReturn(false)

        assert.that(
                set.hashCode() == database.set(set.layer + 1).hashCode(),
                equalTo(false))
    }

    @Test
    fun shouldFindEntry() {
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(database.entry(set.layer, "foo")),
                equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doNothing().whenever(database).deleteOne(set.layer, "foo")
        doReturn("3").whenever(database).selectOne(set.layer, "foo")
        whenever(selectKeysResults.next()).thenReturn(true, false)
        whenever(selectKeysResults.row).thenReturn(1)
        whenever(selectKeysResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(database.entry(set.layer, "foo")),
                equalTo(true))

        verify(database).deleteOne(set.layer, "foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().whenever(database).deleteOne(set.layer, "foo")
        doReturn(null, "3").whenever(database).selectOne(set.layer, "foo")

        val changed = set.add(database.entry(set.layer, "foo"))

        assert.that(changed, equalTo(true))
    }
}
