package hm.binkley.knapsack

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class Database(private val connection: Connection)
    : AutoCloseable {
    override fun close() = connection.close()

    private val countList: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("count-list").oneLine())
    }
    private val countMap: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("count-map").oneLine())
    }
    private val selectLayerKeys: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("select-layer-keys").oneLine())
    }
    private val selectOne: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("select-one").oneLine())
    }
    private val upsertOne: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("upsert-one").oneLine())
    }
    private val deleteOne: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("delete-one").oneLine())
    }

    fun countList() = countList.executeQuery().use { results ->
        oneOnly(results) {
            it.getInt("size")
        }
    }

    fun countMap(layer: Int): Int {
        countMap.setInt(1, layer)
        return countMap.executeQuery().use { results ->
            oneOnly(results) {
                it.getInt("size")
            }
        }
    }

    fun selectLayerKeys(layer: Int): MutableIterator<String> {
        val keys = ArrayList<String>()
        selectLayerKeys.setInt(1, layer)
        selectLayerKeys.executeQuery().use { results ->
            while (results.next())
                keys.add(results.getString("key"))
        }
        return keys.iterator()
    }

    fun selectOne(layer: Int, key: String): String? {
        selectOne.setInt(1, layer)
        selectOne.setString(2, key)
        return selectOne.executeQuery().use { results ->
            oneOnly(results, { -> null }) {
                it.getString("value")
            }
        }
    }

    fun upsertOne(layer: Int, key: String, newValue: String) {
        upsertOne.setInt(1, layer)
        upsertOne.setString(2, key)
        upsertOne.setString(3, newValue)
        upsertOne.executeUpdate()
    }

    fun deleteOne(layer: Int, key: String) {
        deleteOne.setInt(1, layer)
        deleteOne.setString(2, key)
        deleteOne.executeUpdate()
    }

    fun loadSchema() {
        connection.createStatement().use { statement ->
            SQLReader("schema").lines().
                    forEach { statement.executeUpdate(it) }
        }
    }

    fun reset() {
        connection.createStatement().use { statement ->
            statement.executeUpdate(SQLReader("delete-all").oneLine())
        }
    }

    fun <T> transaction(block: () -> T): T {
        connection.autoCommit = false
        try {
            val value = block()
            connection.commit()
            return value
        } catch (e: SQLException) {
            connection.rollback()
            throw e
        } finally {
            connection.autoCommit = true
        }
    }

    companion object {
        private fun <T> oneOnly(
                results: ResultSet,
                defaultValue: () -> T = { ->
                    throw IllegalStateException(
                            "No row results, expected exactly 1")
                },
                getter: (ResultSet) -> T): T {
            if (!results.next())
                return defaultValue()
            val value = getter(results)
            if (results.next()) throw IllegalStateException(
                    "Multiple result rows, expected exactly 1")
            return value
        }
    }
}
