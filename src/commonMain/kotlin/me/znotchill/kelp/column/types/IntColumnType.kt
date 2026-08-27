package me.znotchill.kelp.column.types

import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect

object IntColumnType : ColumnType<Int> {
    override fun sqlType(dialect: Dialect): String = when (dialect) {
        is PostgresDialect -> "INTEGER"
        is MySqlDialect -> "INT"
        else -> "INTEGER"
    }
    override fun toDatabase(value: Int, dialect: Dialect): Any = value
    override fun fromDatabase(value: Any?, dialect: Dialect): Int =
        value.toString().toInt()
}