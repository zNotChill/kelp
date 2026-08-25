package me.znotchill.database

import io.github.smyrgeorge.sqlx4k.postgres.PostgreSQL
import kotlinx.coroutines.runBlocking
import me.znotchill.database.UserModel.where
import me.znotchill.database.conditions.eq

fun main() = runBlocking {
    val db = Database(
        PostgreSQL(
            url = "postgresql://localhost:5432/mydb",
            username = "user",
            password = "password"
        )
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
        age = 67
    )
    UserModel.insert(db, user)
    val test = UserModel.where(db) {
        age eq 67
    }

    println(test)

    Unit
}