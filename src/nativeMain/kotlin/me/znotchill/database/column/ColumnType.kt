package me.znotchill.database.column

interface ColumnType<T> {
    val sqlType: String
    fun fromDatabase(value: Any?): T
    fun toDatabase(value: T): Any?
}

fun <T> ColumnType<T>.nullable(): ColumnType<T?> = object : ColumnType<T?> {
    override val sqlType: String = this@nullable.sqlType

    override fun fromDatabase(value: Any?): T? {
        if (value == null) return null
        return this@nullable.fromDatabase(value)
    }

    override fun toDatabase(value: T?): Any? {
        if (value == null) return null
        return this@nullable.toDatabase(value)
    }
}