package hm.binkley.knapsack

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteDataSource
import java.sql.Connection.TRANSACTION_SERIALIZABLE
import kotlin.collections.MutableMap.MutableEntry

class Knapsack : AbstractMutableMap<String, Any>() {
    private val delegate: MutableMap<String, Any> = linkedMapOf()

    override fun put(key: String, value: Any): Any? = delegate.put(key, value)

    override val entries: MutableSet<MutableEntry<String, Any>>
        get() = delegate.entries

    fun <T> execute(statement: Transaction.() -> T): T {
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite::memory:"
        Database.connect(dataSource)
        return transaction(TRANSACTION_SERIALIZABLE, 0) {
            create(Layer)
            statement.invoke(this)
        }
    }

    object Layer : Table() {
        val key = text("key").primaryKey()
        val value = text("value")
    }
}
