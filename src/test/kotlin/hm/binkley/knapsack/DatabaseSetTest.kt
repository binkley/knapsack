package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseSetTest {
    @Mock private lateinit var database: Connection
    private lateinit var loader: SQLLoader
    @Mock private lateinit var countAll: PreparedStatement
    @Mock private lateinit var countResult: ResultSet
    @Mock private lateinit var selectAll: PreparedStatement
    @Mock private lateinit var allResults: ResultSet
    @Mock private lateinit var selectOne: PreparedStatement
    @Mock private lateinit var oneResult: ResultSet
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        loader = spy(SQLLoader(database))

        doReturn(selectOne).`when`(loader).selectOne
        doReturn(deleteOne).`when`(loader).deleteOne
        doReturn(countAll).`when`(loader).countAll
        doReturn(selectAll).`when`(loader).selectAll

        `when`(countAll.executeQuery()).thenReturn(countResult)
        `when`(selectAll.executeQuery()).thenReturn(allResults)
        `when`(selectOne.executeQuery()).thenReturn(oneResult)

        set = DatabaseSet(loader)
    }

    @Test
    fun shouldClose() {
        `when`(countResult.next()).thenReturn(true, false)
        `when`(countResult.getInt(eq("size"))).thenReturn(0)

        set.size

        verify(countResult, atLeastOnce()).close()
    }

    @Test
    fun shouldStartEmptySized() {
        `when`(countResult.next()).thenReturn(true, false)
        `when`(countResult.getInt(eq("size"))).thenReturn(0)

        assert.that(set.size, equalTo(0))
    }

    @Test(expected = IllegalStateException::class)
    fun shouldFailIfSizeSqlHasNoResults() {
        `when`(countResult.next()).thenReturn(false)

        set.size
    }

    @Test(expected = IllegalStateException::class)
    fun shouldFailIfSizeSqlHasTooManyResults() {
        `when`(countResult.next()).thenReturn(true, true, false)

        set.size
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(allResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        `when`(countResult.next()).thenReturn(true, false, true, false)
        `when`(countResult.getInt(eq("size"))).thenReturn(0)

        assert.that(set,
                equalTo(DatabaseSet(loader)))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        assert.that(set.hashCode(),
                equalTo(DatabaseSet(loader).hashCode()))
    }

    @Test
    fun shouldFindEntry() {
        `when`(allResults.next()).thenReturn(true, false)
        `when`(allResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(DatabaseEntry("foo", loader)),
                equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        `when`(allResults.next()).thenReturn(true, false)
        `when`(allResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(DatabaseEntry("foo", loader)),
                equalTo(true))

        verify(deleteOne).setString(1, "foo")
        verify(deleteOne).executeUpdate()
    }

    @Test
    fun shouldMutate() {
        `when`(oneResult.next()).thenReturn(false, true, false)
        `when`(oneResult.getString(eq("value"))).thenReturn("3")

        val changed = set.add(DatabaseEntry("foo", loader))

        assert.that(changed, equalTo(true))
    }
}
