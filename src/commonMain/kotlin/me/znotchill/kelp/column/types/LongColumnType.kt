package me.znotchill.kelp.column.types

import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect
import me.znotchill.kelp.dialects.SqliteDialect

object LongColumnType : ColumnType<Long> {
    override fun sqlType(dialect: Dialect): String = when (dialect) {
        is PostgresDialect -> "BIGINT"
        is MySqlDialect -> "BIGINT"
        is SqliteDialect -> "INTEGER"
        else -> "BIGINT"
    }

    override fun toDatabase(value: Long, dialect: Dialect): Any = value

    override fun fromDatabase(value: Any?, dialect: Dialect): Long =
        value?.toString()?.toLong() ?: 0L
}