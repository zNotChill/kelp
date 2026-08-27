package me.znotchill.kelp.column.types

import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect
import me.znotchill.kelp.dialects.SqliteDialect

object DoubleColumnType : ColumnType<Double> {
    override fun sqlType(dialect: Dialect): String = when (dialect) {
        is PostgresDialect -> "DOUBLE PRECISION"
        is MySqlDialect -> "DOUBLE"
        is SqliteDialect -> "REAL"
        else -> "DOUBLE PRECISION"
    }

    override fun toDatabase(value: Double, dialect: Dialect): Any = value

    override fun fromDatabase(value: Any?, dialect: Dialect): Double =
        value?.toString()?.toDouble() ?: 0.0
}