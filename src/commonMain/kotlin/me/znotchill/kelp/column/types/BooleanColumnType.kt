package me.znotchill.kelp.column.types

import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect
import me.znotchill.kelp.dialects.SqliteDialect

object BooleanColumnType : ColumnType<Boolean> {
    override fun sqlType(dialect: Dialect): String = when (dialect) {
        is PostgresDialect -> "BOOLEAN"
        is MySqlDialect -> "BOOLEAN"
        is SqliteDialect -> "INTEGER"
        else -> "BOOLEAN"
    }

    override fun toDatabase(value: Boolean, dialect: Dialect): Any = value

    override fun fromDatabase(value: Any?, dialect: Dialect): Boolean =
        when (val v = value) {
            is Boolean -> v
            is Number -> v.toInt() != 0
            else -> when (v.toString().lowercase()) {
                "true", "t", "1" -> true
                "false", "f", "0" -> false
                else -> error("Failed to parse '$v' as Boolean")
            }
        }
}