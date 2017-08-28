package hm.binkley.knapsack

import java.sql.Connection
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.DriverManager.getConnection

class Database(private val database: Connection) : Connection by database {
    companion object {
        fun main(): Database = database(
                "${System.getProperty("user.home")}/.knapsack")

        fun test(): Database = database(
                "${System.getProperty("java.io.tmpdir")}/knapsack")

        private fun database(knapsackDir: String): Database {
            val connection = getConnection("jdbc:hsqldb:file:$knapsackDir")
            connection.transactionIsolation = TRANSACTION_SERIALIZABLE
            return Database(connection)
        }
    }
}
