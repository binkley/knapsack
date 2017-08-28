package hm.binkley.knapsack

import java.sql.Connection
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import java.sql.DriverManager.getConnection

class Database(private val database: Connection) : Connection by database {
    companion object {
        fun main(): Database = database(System.getProperty("user.home"))

        fun test(): Database = database(System.getProperty("java.io.tmpdir"))

        private fun database(knapsackDir: String): Database {
            val connection = getConnection(
                    "jdbc:hsqldb:file:$knapsackDir/knapsack/db")
            connection.transactionIsolation = TRANSACTION_SERIALIZABLE
            return Database(connection)
        }
    }
}
