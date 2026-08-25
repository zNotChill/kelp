package me.znotchill.kelp.column.types

import me.znotchill.kelp.column.ColumnType

object StringColumnType : ColumnType<String> {
    override val sqlType = "TEXT"
    override fun fromDatabase(value: Any?): String =
        value as String

    override fun toDatabase(value: String): Any =
        value
}