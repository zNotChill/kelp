package me.znotchill.database

data class User(
    val id: String,
    val name: String,
    val age: Int?
)

object UserModel : Model<User>("users") {
    val id = column("id") { it.id }
    val name = column("name") { it.name }
    val age = nullable("age") { it.age }

    override fun decode(row: Row): User =
        User(
            id = row[id],
            name = row[name],
            age = row[age]
        )
}