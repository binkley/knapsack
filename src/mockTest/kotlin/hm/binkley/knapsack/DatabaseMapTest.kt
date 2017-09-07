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
internal class DatabaseMapTest {
    @Mock private lateinit var connection: Connection
    @Spy
    @InjectMocks private lateinit var database: Database
    @Mock private lateinit var selectKeysResults: ResultSet
    @Mock private lateinit var otherSelectKeysResults: ResultSet
    private lateinit var map: DatabaseMap

    @Before
    fun setUp() {
        val layer = 0
        doReturn(selectKeysResults).`when`(database).selectMapKeys(layer)
        doReturn(otherSelectKeysResults).`when`(database).selectMapKeys(
                layer + 1)
        map = database.map(layer)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).`when`(database).countAll(map.layer)

        assert.that(map.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        `when`(selectKeysResults.next()).thenReturn(false)

        assert.that(map.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).`when`(database).countAll(map.layer)

        assert.that(map == database.map(map.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(map == database.map(map.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        `when`(selectKeysResults.next()).thenReturn(false, false)

        assert.that(map.hashCode() == database.map(map.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        `when`(selectKeysResults.next()).thenReturn(false)
        `when`(otherSelectKeysResults.next()).thenReturn(false)

        assert.that(
                map.hashCode() == database.map(map.layer + 1).hashCode(),
                equalTo(false))
    }
}
