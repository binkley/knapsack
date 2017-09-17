package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Connection
import java.sql.ResultSet

@RunWith(MockitoJUnitRunner::class)
internal class DatabaseMapMockTest {
    private val connection: Connection = mock()
    private val database = spy(Database(connection))
    private val selectKeysResults: ResultSet = mock()
    private val otherSelectKeysResults: ResultSet = mock()
    private val map = database.map(0)

    @Before
    fun setUp() {
        doReturn(selectKeysResults).whenever(database).selectMapKeys(
                map.layer)
        doReturn(otherSelectKeysResults).whenever(database).selectMapKeys(
                map.layer + 1)
    }

    @Test
    fun shouldStartEmptySized() {
        doReturn(0).whenever(database).countMap(map.layer)

        assert.that(map.size, equalTo(0))
    }

    @Test
    fun shouldStartEmptyIterated() {
        whenever(selectKeysResults.next()).thenReturn(false)

        assert.that(map.iterator().hasNext(), equalTo(false))
    }

    @Test
    fun shouldEqualsWhenEmpty() {
        doReturn(0, 0).whenever(database).countMap(map.layer)

        assert.that(map == database.map(map.layer), equalTo(true))
    }

    @Test
    fun shouldNotEqualsWhenEmpty() {
        assert.that(map == database.map(map.layer + 1), equalTo(false))
    }

    @Test
    fun shouldHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false, false)

        assert.that(map.hashCode() == database.map(map.layer).hashCode(),
                equalTo(true))
    }

    @Test
    fun shouldNotHashCodeWhenEmpty() {
        whenever(selectKeysResults.next()).thenReturn(false)
        whenever(otherSelectKeysResults.next()).thenReturn(false)

        assert.that(
                map.hashCode() == database.map(map.layer + 1).hashCode(),
                equalTo(false))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun shouldThrowOnNegativeLayer() {
        database.map(-1)
    }
}
