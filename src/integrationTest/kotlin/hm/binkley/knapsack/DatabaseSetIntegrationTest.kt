package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import kotlin.collections.MutableMap.MutableEntry
import kotlin.test.fail

class DatabaseSetIntegrationTest {
    private val sql = SQLLoader()
    private lateinit var database: Connection

    @Before
    fun setUpDatabase() {
        database = DriverManager.getConnection("jdbc:hsqldb:mem:knapsack")
        database.createStatement().executeUpdate(sql.schema())
    }

    @After
    fun tearDownDatabase() {
        database.createStatement().executeUpdate(sql.deleteAll())
        database.close()
    }

    @Test
    fun shouldWorkEndToEnd() {
        val countAll = database.prepareStatement(sql.countAll())
        val selectAll = database.prepareStatement(sql.selectAll())
        val selectOne = database.prepareStatement(sql.selectOne())
        val upsertOne = database.prepareStatement(sql.upsertOne())
        val deleteOne = database.prepareStatement(sql.deleteOne())

        val set = DatabaseSet(countAll, selectAll, selectOne, upsertOne,
                deleteOne)

        assert.that(set.isEmpty(), equalTo(true))
        assert.that(set.add(entryOf("foo", "3")), equalTo(true))
        assert.that(set.add(entryOf("foo", "3")), equalTo(false))
        assert.that(set.add(entryOf("foo", "7")), equalTo(true))
        assert.that(set.size, equalTo(1))
        assert.that(set.add(entryOf("bar", "3")), equalTo(true))
        assert.that(set.add(entryOf("bar", "3")), equalTo(false))
        assert.that(set.add(entryOf("bar", "15")), equalTo(true))
        assert.that(set.size, equalTo(2))

        val sit = set.iterator()
        while (sit.hasNext()) {
            val e = sit.next()
            when (e.key) {
                "foo" -> {
                    assert.that(e.value, equalTo("7"))
                    sit.remove()
                }
                "bar" -> assert.that(e.value, equalTo("15"))
                else -> fail()
            }
        }

        assert.that(set.size, equalTo(1))
        assert.that(set.map(Entry::key).first(), equalTo("bar"))
    }

    companion object {
        private fun entryOf(key: String, value: String)
                : MutableEntry<String, String?> {
            return object : MutableEntry<String, String?> {
                override val value: String?
                    get() {
                        return value
                    }

                override fun setValue(newValue: String?): String? {
                    throw UnsupportedOperationException()
                }

                override val key: String
                    get() = key
            }
        }
    }
}
