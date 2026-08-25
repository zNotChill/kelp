package me.znotchill.kelp

import io.github.smyrgeorge.sqlx4k.Driver
import io.github.smyrgeorge.sqlx4k.SQLError
import io.github.smyrgeorge.sqlx4k.Statement
import io.github.smyrgeorge.sqlx4k.Transaction
import me.znotchill.kelp.exceptions.AlreadyExistsException

class Database(
    val driver: Driver
) {
    suspend fun execute(statement: Statement): Result<Long> {
        println("Executing $statement")
        return driver.execute(statement)
    }

    suspend fun tryExecute(statement: String): Long =
        tryExecute(Statement.create(statement))

    suspend fun tryExecute(statement: Statement): Long {
        return try {
            execute(statement).getOrThrow()
        } catch (e: SQLError) {
            if (e.message == null) throw e
            val msg = e.message!!

            if (msg.contains("already exists at"))
                throw AlreadyExistsException(msg)
            return 0
        }
    }

    suspend fun createTable(table: Model<*>) {
        tryExecute(table.createStatement())
    }

    suspend fun dropTable(table: Model<*>) {
        tryExecute(table.dropStatement())
    }

    suspend fun <T> transaction(
        block: suspend Transaction.() -> T
    ): T {
        return driver.transaction(block)
    }

}