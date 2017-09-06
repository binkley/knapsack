package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
class DatabaseMapListTest {
    @Mock private lateinit var connection: Connection
    @Spy
    @InjectMocks private lateinit var database: Database
    @Mock private lateinit var selectKeysResults: ResultSet
    private lateinit var mapList: DatabaseMapList

    @Before
    fun setUp() {
        doReturn(selectKeysResults).`when`(database).selectKeys(0)
        mapList = database.mapList()
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).`when`(database).countAll(0)

        assert.that(mapList.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectKeysResults.next()).thenReturn(false)

        assert.that(mapList.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).`when`(database).countAll(0)

        assert.that(mapList == database.mapList(), equalTo(true))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        `when`(selectKeysResults.next()).thenReturn(false, false)

        assert.that(mapList.hashCode() == database.mapList().hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldBeginAtLayerZero() {
        assert.that(mapList.layer, equalTo(0))
    }

    @Test
    fun shouldIncrementLayerAfterNext() {
        mapList.next()

        assert.that(mapList.layer, equalTo(1))
    }
}
