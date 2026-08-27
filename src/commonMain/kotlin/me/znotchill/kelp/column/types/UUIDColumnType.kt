package me.znotchill.kelp.column.types

import kotlin.uuid.Uuid
import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect
import me.znotchill.kelp.dialects.MySqlDialect
import me.znotchill.kelp.dialects.PostgresDialect
import me.znotchill.kelp.dialects.SqliteDialect

object UUIDColumnType : ColumnType<Uuid> {

    override fun sqlType(dialect: Dialect): String =
        when (dialect) {
            is PostgresDialect -> "UUID"
            is MySqlDialect -> "BINARY(16)"
            is SqliteDialect -> "BLOB"
            else -> error("UUID is not supported by ${dialect::class.simpleName}")
        }

    override fun toDatabase(
        value: Uuid,
        dialect: Dialect
    ): Any =
        when (dialect) {
            is PostgresDialect -> value.toString()
            is MySqlDialect,
            is SqliteDialect -> value.toByteArray()
            else -> error("UUID is not supported by ${dialect::class.simpleName}")
        }

    override fun fromDatabase(
        value: Any?,
        dialect: Dialect
    ): Uuid =
        when (dialect) {
            is PostgresDialect ->
                Uuid.parse(value.toString())

            is MySqlDialect,
            is SqliteDialect -> {
                require(value is ByteArray) {
                    "Expected ByteArray, got ${value?.let { it::class.simpleName }}"
                }

                Uuid.fromByteArray(value)
            }

            else ->
                error("UUID is not supported by ${dialect::class.simpleName}")
        }
}