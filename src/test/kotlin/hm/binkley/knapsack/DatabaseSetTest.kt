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
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseSetTest {
    @Mock private lateinit var database: Database
    @Spy
    @InjectMocks private lateinit var loader: SQLLoader
    @Mock private lateinit var countAll: PreparedStatement
    @Mock private lateinit var countAllResults: ResultSet
    @Mock private lateinit var selectAll: PreparedStatement
    @Mock private lateinit var selectAllResults: ResultSet
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        doReturn(deleteOne).`when`(loader).deleteOne
        doReturn(countAll).`when`(loader).countAll
        doReturn(selectAll).`when`(loader).selectAll

        `when`(countAll.executeQuery()).thenReturn(countAllResults)
        `when`(selectAll.executeQuery()).thenReturn(selectAllResults)

        set = DatabaseSet(loader)
    }

    @Test
    fun shouldClose() {
        `when`(countAllResults.next()).thenReturn(true, false)
        `when`(countAllResults.getInt(eq("size"))).thenReturn(0)

        set.size

        verify(countAllResults, atLeastOnce()).close()
    }

    @Test
    fun shouldStartEmptySized() {
        `when`(countAllResults.next()).thenReturn(true, false)
        `when`(countAllResults.getInt(eq("size"))).thenReturn(0)

        assert.that(set.size, equalTo(0))
    }

    @Test(expected = IllegalStateException::class)
    fun shouldFailIfSizeSqlHasNoResults() {
        `when`(countAllResults.next()).thenReturn(false)

        set.size
    }

    @Test(expected = IllegalStateException::class)
    fun shouldFailIfSizeSqlHasTooManyResults() {
        `when`(countAllResults.next()).thenReturn(true, true, false)

        set.size
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectAllResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        `when`(countAllResults.next()).thenReturn(true, false, true, false)
        `when`(countAllResults.getInt(eq("size"))).thenReturn(0)

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
        `when`(selectAllResults.next()).thenReturn(true, false)
        `when`(selectAllResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.contains(DatabaseEntry("foo", loader)),
                equalTo(true))
    }

    @Test
    fun shouldRemoveEntry() {
        doReturn("3").`when`(loader).selectOne("foo")
        `when`(selectAllResults.next()).thenReturn(true, false)
        `when`(selectAllResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(DatabaseEntry("foo", loader)),
                equalTo(true))

        verify(deleteOne).setString(1, "foo")
        verify(deleteOne).executeUpdate()
    }

    @Test
    fun shouldMutate() {
        doReturn(null, "3").`when`(loader).selectOne("foo")

        val changed = set.add(DatabaseEntry("foo", loader))

        assert.that(changed, equalTo(true))
    }
}
