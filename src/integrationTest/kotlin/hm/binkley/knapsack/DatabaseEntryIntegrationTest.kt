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
    private lateinit var database: Connection

    @Before
    fun setUpDatabase() {
        database = getConnection("jdbc:hsqldb:mem:knapsack")
        database.createStatement().executeUpdate(schemaSQL())
    }

    @After
    fun tearDownDatabase() {
        database.createStatement().executeUpdate(deleteAllSQL())
        database.close()
    }

    @Test
    fun shouldWorkEndToEnd() {
        database.createStatement().executeUpdate(schemaSQL())
        val select = database.prepareStatement(selectOneSQL())
        val upsert = database.prepareStatement(upsertOneSQL())
        val delete = database.prepareStatement(deleteOneSQL())

        val entry = DatabaseEntry("foo", select, upsert, delete)

        assert.that(entry.value, absent())
        assert.that(entry.setValue("3"), absent())
        assert.that(entry.setValue("4"), equalTo("3"))
        assert.that(entry.value, equalTo("4"))
    }

    private fun schemaSQL()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-schema.sql").
            readText()

    private fun selectOneSQL()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-select-one.sql").
            readText()

    private fun upsertOneSQL()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-upsert-one.sql").
            readText()

    private fun deleteOneSQL()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-delete-one.sql").
            readText()

    private fun deleteAllSQL()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-delete-all.sql").
            readText()
}
