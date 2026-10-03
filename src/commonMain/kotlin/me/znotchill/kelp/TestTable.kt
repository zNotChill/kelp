package me.znotchill.kelp

data class User(
    val id: String,
    val name: String,
    val age: Int?,
    val joinDate: Long
)

object UserModel : Model<User>("users") {
    val id = column("id") { it.id }
    val name = column("name") { it.name }
    val age = nullable("age") { it.age }
    val joinDate = column("join_date") { it.joinDate }

    override fun decode(row: Row): User =
        User(
            id = row[id],
            name = row[name],
            age = row[age],
            joinDate = row[joinDate]
        )
}