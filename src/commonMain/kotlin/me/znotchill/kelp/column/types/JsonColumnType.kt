package me.znotchill.kelp.column.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect

class JsonColumnType<T>(
    override val serializer: KSerializer<T>,
    private val json: Json = Json
) : ColumnType<T> {

    override fun sqlType(dialect: Dialect): String =
        dialect.jsonType()

    override fun toDatabase(value: T, dialect: Dialect): Any =
        json.encodeToString(serializer, value)

    override fun fromDatabase(value: Any?, dialect: Dialect): T {
        requireNotNull(value) { "JSON column returned null" }

        return json.decodeFromString(
            serializer,
            value.toString()
        )
    }
}