package me.znotchill.kelp

import io.github.smyrgeorge.sqlx4k.postgres.PostgreSQL
import kotlinx.coroutines.runBlocking
import me.znotchill.kelp.UserModel.where
import me.znotchill.kelp.conditions.and
import me.znotchill.kelp.conditions.between
import me.znotchill.kelp.conditions.isLatest
import me.znotchill.kelp.conditions.neq
import me.znotchill.kelp.dialects.PostgresDialect

fun main() = runBlocking {
    val db = Database(
        PostgreSQL(
            url = "postgresql://localhost:5432/mydb",
            username = "user",
            password = "password"
        ),
        PostgresDialect
    )

    try {
        db.dropTable(UserModel)
        db.createTable(UserModel)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val user = User(
        id = "hi",
        name = "hello",
        age = 67,
        joinDate = 0
    )
    UserModel.insert(db, user)
    val test = UserModel.where(db) {
        joinDate.isLatest
    }

    println(test)

    Unit
}