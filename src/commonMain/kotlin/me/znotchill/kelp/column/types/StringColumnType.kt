package me.znotchill.kelp.column.types

import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect
import me.znotchill.kelp.dialects.SqliteDialect

object StringColumnType : ColumnType<String> {
    override fun sqlType(dialect: Dialect): String = when (dialect) {
        is PostgresDialect -> "TEXT"
        is MySqlDialect -> "TEXT"
        is SqliteDialect -> "TEXT"
        else -> "TEXT"
    }

    override fun toDatabase(value: String, dialect: Dialect): Any = value

    override fun fromDatabase(value: Any?, dialect: Dialect): String =
        value?.toString() ?: ""
}