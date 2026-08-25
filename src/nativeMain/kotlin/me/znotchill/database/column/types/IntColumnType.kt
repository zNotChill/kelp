package me.znotchill.database.column.types

import me.znotchill.database.column.ColumnType

object IntColumnType : ColumnType<Int> {
    override val sqlType = "INTEGER"

    override fun fromDatabase(value: Any?): Int =
        value.toString().toInt()

    override fun toDatabase(value: Int): Any =
        value
}