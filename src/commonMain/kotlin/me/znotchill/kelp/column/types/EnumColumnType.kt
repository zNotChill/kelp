package me.znotchill.kelp.column.types

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.dialects.Dialect

class EnumColumnType<T : Enum<T>>(
    override val serializer: KSerializer<T>,
    private val values: Array<T>
) : ColumnType<T> {

    override fun sqlType(dialect: Dialect): String = "VARCHAR"

    override fun toDatabase(value: T, dialect: Dialect): Any =
        value.name

    @OptIn(ExperimentalSerializationApi::class)
    override fun fromDatabase(value: Any?, dialect: Dialect): T {
        val name = value?.toString()
            ?: error("Expected non-null enum value")

        return values.firstOrNull { it.name == name }
            ?: error(
                "Unknown enum value '$name' for ${serializer.descriptor.serialName}"
            )
    }
}