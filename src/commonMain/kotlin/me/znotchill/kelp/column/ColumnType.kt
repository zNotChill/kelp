package me.znotchill.kelp.column

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import me.znotchill.kelp.dialects.Dialect

interface ColumnType<T> {
    val serializer: KSerializer<T>

    fun sqlType(dialect: Dialect): String
    fun toDatabase(value: T, dialect: Dialect): Any?
    fun fromDatabase(value: Any?, dialect: Dialect): T
}

fun <T : Any> ColumnType<T>.nullable(): ColumnType<T?> =
    object : ColumnType<T?> {
        override val serializer: KSerializer<T?> =
            this@nullable.serializer.nullable

        override fun sqlType(dialect: Dialect): String =
            this@nullable.sqlType(dialect)

        override fun fromDatabase(value: Any?, dialect: Dialect): T? {
            if (value == null) return null
            return this@nullable.fromDatabase(value, dialect)
        }

        override fun toDatabase(value: T?, dialect: Dialect): Any? {
            if (value == null) return null
            return this@nullable.toDatabase(value, dialect)
        }
    }