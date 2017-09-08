package hm.binkley.knapsack

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class Database(
        private val connection: Connection) : AutoCloseable, Cloneable {
    override fun close() = connection.close()

    override public fun clone()
            = Database(DriverManager.getConnection(connection.metaData.url))

    private val countMap: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("count-map").oneLine())
    }
    private val selectMapKeys: PreparedStatement by lazy {
        connection.prepareStatement(SQLReader("select-map-keys").oneLine())
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

    fun countMap(layer: Int): Int {
        countMap.setInt(1, layer)
        countMap.executeQuery().use { results ->
            if (!results.next()) throw IllegalStateException()
            val count = results.getInt("size")
            if (results.next()) throw IllegalStateException()
            return count
        }
    }

    fun selectMapKeys(layer: Int): ResultSet {
        selectMapKeys.setInt(1, layer)
        return selectMapKeys.executeQuery()
    }

    fun selectOne(layer: Int, key: String): String? {
        selectOne.setInt(1, layer)
        selectOne.setString(2, key)
        selectOne.executeQuery().use { results ->
            if (!results.next()) return null
            val value = results.getString("value")
            if (results.next()) throw IllegalStateException()
            return value
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
        connection.createStatement().use {
            it.executeUpdate(SQLReader("delete-all").oneLine())
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
}
