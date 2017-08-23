package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import hm.binkley.knapsack.DatabaseEntry.Companion.VALUE_COLUMN
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.sql.PreparedStatement
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseSetTest {
    @Mock private lateinit var countAll: PreparedStatement
    @Mock private lateinit var countResult: ResultSet
    @Mock private lateinit var selectAll: PreparedStatement
    @Mock private lateinit var allResults: ResultSet
    @Mock private lateinit var selectOne: PreparedStatement
    @Mock private lateinit var oneResult: ResultSet
    @Mock private lateinit var upsertOne: PreparedStatement
    @Mock private lateinit var deleteOne: PreparedStatement
    private lateinit var set: DatabaseSet

    @Before
    fun setUpDatabase() {
        `when`(countAll.executeQuery()).thenReturn(countResult)
        `when`(selectAll.executeQuery()).thenReturn(allResults)
        `when`(selectOne.executeQuery()).thenReturn(oneResult)

        set = DatabaseSet(countAll, selectAll, selectOne, upsertOne, deleteOne)
    }

    @Test
    fun shouldStartEmptySized() {
        `when`(countResult.next()).thenReturn(true, false)
        `when`(countResult.getInt(eq("size"))).thenReturn(0)

        assert.that(set.size, equalTo(0))
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
                equalTo(DatabaseSet(countAll, selectAll, selectOne, upsertOne,
                        deleteOne)))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        assert.that(set.hashCode(),
                equalTo(DatabaseSet(countAll, selectAll, selectOne, upsertOne,
                        deleteOne).hashCode()))
    }

    @Test
    fun shouldMutate() {
        `when`(oneResult.next()).thenReturn(false, true, false)
        `when`(oneResult.getString(eq(VALUE_COLUMN))).thenReturn("3")

        val changed = set.add(
                DatabaseEntry("foo", selectOne, upsertOne, deleteOne))

        assert.that(changed, equalTo(true))
    }
}
