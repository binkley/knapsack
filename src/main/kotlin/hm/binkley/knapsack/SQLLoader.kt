package hm.binkley.knapsack

import java.sql.Connection
import java.sql.PreparedStatement

class SQLLoader(private val connection: Connection) {
    val prepareCountAll: PreparedStatement by lazy {
        connection.prepareStatement(readSql("count-all"))
    }
    val prepareSelectAll: PreparedStatement by lazy {
        connection.prepareStatement(readSql("select-all"))
    }
    val prepareSelectOne: PreparedStatement by lazy {
        connection.prepareStatement(readSql("select-one"))
    }
    val prepareUpsertOne: PreparedStatement by lazy {
        connection.prepareStatement(readSql("upsert-one"))
    }
    val prepareDeleteOne: PreparedStatement by lazy {
        connection.prepareStatement(readSql("delete-one"))
    }

    fun loadSchema() {
        connection.createStatement().use {
            it.executeUpdate(readSql("schema"))
        }
    }

    fun reset() {
        connection.createStatement().use {
            it.executeUpdate(readSql("delete-all"))
        }
    }

    private fun readSql(purpose: String): String {
        return javaClass.
                getResource("/hm/binkley/knapsack/knapsack-$purpose.sql").
                readText()
    }
}
