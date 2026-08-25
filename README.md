
# Kelp

my version of exposed but for full multiplatform support :) (WIP)

not really that similar to exposed, but it goes with a design that I personally like
way more than exposed

realistically, this is probably a lot worse than exposed, but I made this for people
like me who need a multiplatform database system 

# Install

not published yet

# Quick start

## Connecting
```kt
val db = Database(
    PostgreSQL(
        url = "postgresql://localhost:5432/mydb",
        username = "user",
        password = "password"
    )
)
```

## Table models
```kt
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
```

## Table management
```kt
try {
    db.dropTable(UserModel)
    db.createTable(UserModel)
} catch (e: Exception) {
    e.printStackTrace()
}
```

## Row management
```kt
val user = User(id = "hi", name = "hello", age = 67)
db.insert(db, user)
```
```kt
val results = UserModel.where(db) {
    (age between 18..65) and (name eq "hello")
}
```