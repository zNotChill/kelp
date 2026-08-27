package me.znotchill.kelp.column

import me.znotchill.kelp.dialects.Dialect

interface ColumnType<T> {
    fun sqlType(dialect: Dialect): String
    fun fromDatabase(value: Any?, dialect: Dialect): T
    fun toDatabase(value: T, dialect: Dialect): Any?
}

fun <T> ColumnType<T>.nullable(): ColumnType<T?> = object : ColumnType<T?> {
    override fun sqlType(dialect: Dialect): String = this@nullable.sqlType(dialect)

    override fun fromDatabase(value: Any?, dialect: Dialect): T? {
        if (value == null) return null
        return this@nullable.fromDatabase(value, dialect)
    }

    override fun toDatabase(value: T?, dialect: Dialect): Any? {
        if (value == null) return null
        return this@nullable.toDatabase(value, dialect)
    }
}