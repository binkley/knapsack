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
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseSetTest {
    @Mock private lateinit var database: Database
    @Spy
    @InjectMocks private lateinit var loader: SQLLoader
    @Mock private lateinit var selectAll: PreparedStatement
    @Mock private lateinit var selectAllResults: ResultSet
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        doReturn(selectAll).`when`(loader).selectAll

        `when`(selectAll.executeQuery()).thenReturn(selectAllResults)

        set = DatabaseSet(loader)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).`when`(loader).countAll()

        assert.that(set.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectAllResults.next()).thenReturn(false)

        assert.that(set.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).`when`(loader).countAll()

        assert.that(set, equalTo(DatabaseSet(loader)))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        assert.that(set.hashCode(), equalTo(DatabaseSet(loader).hashCode()))
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
        doNothing().`when`(loader).deleteOne("foo")
        doReturn("3").`when`(loader).selectOne("foo")
        `when`(selectAllResults.next()).thenReturn(true, false)
        `when`(selectAllResults.getString(eq("key"))).thenReturn("foo")

        assert.that(set.remove(DatabaseEntry("foo", loader)),
                equalTo(true))

        verify(loader).deleteOne("foo")
    }

    @Test
    fun shouldMutate() {
        doNothing().`when`(loader).deleteOne("foo")
        doReturn(null, "3").`when`(loader).selectOne("foo")

        val changed = set.add(DatabaseEntry("foo", loader))

        assert.that(changed, equalTo(true))
    }
}
