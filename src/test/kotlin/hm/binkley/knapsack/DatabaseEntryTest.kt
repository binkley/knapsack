package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.sql.PreparedStatement
import java.sql.SQLException

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseEntryTest {
    @Rule
    @JvmField
    val thrown = ExpectedException.none()!!

    @Mock private lateinit var database: Database
    @Spy
    @InjectMocks private lateinit var loader: SQLLoader
    @Mock private lateinit var upsertOne: PreparedStatement
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var entry: DatabaseEntry

    @Before
    fun setUpDatabase() {
        doReturn(upsertOne).`when`(loader).upsertOne
        doReturn(deleteOne).`when`(loader).deleteOne
        doReturn("3").`when`(loader).selectOne("foo")

        entry = DatabaseEntry("foo", loader)
    }

    @Test
    fun shouldGetKey_forJaCoCo() {
        assert.that(entry.key, equalTo("foo"))
    }

    @Test
    fun shouldEquals() {
        assert.that(entry, equalTo(DatabaseEntry("foo", loader)))
    }

    @Suppress("ReplaceCallWithComparison")
    @Test
    fun shouldNotEquals() {
        assert.that(entry.equals(this), equalTo(false))
    }

    @Test
    fun shouldHashCode() {
        assert.that(entry.hashCode(),
                equalTo(DatabaseEntry("foo", loader).hashCode()))
    }

    @Test
    fun shouldGetValue() {
        entry.value

        verify(loader).selectOne("foo")
    }

    @Test
    fun shouldGetNull() {
        doReturn(null).`when`(loader).selectOne("foo")

        assert.that(entry.value, absent())
    }

    @Test
    fun shouldSetValueFirstTime() {
        doReturn(null, "3").`when`(loader).selectOne("foo")

        val previous = entry.setValue("3")

        assert.that(previous, absent())
        assert.that(entry.value, equalTo("3"))
    }

    @Test
    fun shouldSetValueSecondTime() {
        doReturn("2", "3").`when`(loader).selectOne("foo")

        val previous = entry.setValue("3")

        assert.that(previous, equalTo("2"))
        assert.that(entry.value, equalTo("3"))
    }

    @Test
    fun shouldSetNull() {
        doReturn("3", null).`when`(loader).selectOne("foo")

        val previous = entry.setValue(null)

        assert.that(previous, equalTo("3"))
        assert.that(entry.value, absent())
    }

    @Test
    fun shouldCommit() {
        entry.setValue(null)

        verify(database).commit()
    }

    @Test
    fun shouldRollback() {
        thrown.expect(SQLException::class.java)
        `when`(deleteOne.executeUpdate()).thenThrow(SQLException::class.java)

        entry.setValue(null)

        verify(database).rollback()
    }
}
