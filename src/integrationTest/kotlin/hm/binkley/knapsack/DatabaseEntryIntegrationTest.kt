package hm.binkley.knapsack

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager.getConnection

class DatabaseEntryIntegrationTest {
    private val sql = SQLLoader()
    private lateinit var database: Connection

    @Before
    fun setUpDatabase() {
        database = getConnection("jdbc:hsqldb:mem:knapsack")
        database.createStatement().executeUpdate(sql.schema())
    }

    @After
    fun tearDownDatabase() {
        database.createStatement().executeUpdate(sql.deleteAll())
        database.close()
    }

    @Test
    fun shouldWorkEndToEnd() {
        val selectOne = database.prepareStatement(sql.selectOne())
        val upsertOne = database.prepareStatement(sql.upsertOne())
        val deleteOne = database.prepareStatement(sql.deleteOne())

        val entry = DatabaseEntry("foo", selectOne, upsertOne, deleteOne)

        assert.that(entry.value, absent())
        assert.that(entry.setValue("3"), absent())
        assert.that(entry.setValue("4"), equalTo("3"))
        assert.that(entry.value, equalTo("4"))
    }
}
