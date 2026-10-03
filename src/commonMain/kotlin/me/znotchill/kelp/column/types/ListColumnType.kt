package me.znotchill.kelp.column.types

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect
import me.znotchill.kelp.dialects.SqliteDialect

class ListColumnType<T>(
    private val elementType: ColumnType<T>
) : ColumnType<List<T>> {

    override val serializer =
        ListSerializer(elementType.serializer)

    override fun sqlType(dialect: Dialect): String {
        return when (dialect) {
            PostgresDialect -> "${elementType.sqlType(dialect)}[]"
            MySqlDialect -> "JSON"
            SqliteDialect -> "TEXT"
            else -> "TEXT"
        }
    }

    override fun toDatabase(
        value: List<T>,
        dialect: Dialect
    ): Any? {
        return when (dialect) {
            PostgresDialect -> value
            MySqlDialect,
            SqliteDialect -> Json.encodeToString(serializer, value)
            else -> value
        }
    }

    override fun fromDatabase(
        value: Any?,
        dialect: Dialect
    ): List<T> {
        return when (dialect) {
            PostgresDialect -> {
                emptyList()
            }

            MySqlDialect,
            SqliteDialect -> {
                Json.decodeFromString(
                    serializer,
                    value as String
                )
            }

            else -> emptyList()
        }
    }
}