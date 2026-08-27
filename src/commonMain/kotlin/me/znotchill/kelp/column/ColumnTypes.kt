package me.znotchill.kelp.column

import me.znotchill.kelp.column.types.BooleanColumnType
import me.znotchill.kelp.column.types.DoubleColumnType
import me.znotchill.kelp.column.types.IntColumnType
import me.znotchill.kelp.column.types.LongColumnType
import me.znotchill.kelp.column.types.StringColumnType
import me.znotchill.kelp.column.types.UUIDColumnType
import kotlin.reflect.KClass
import kotlin.uuid.Uuid

object ColumnTypes {
    val registry: MutableMap<KClass<*>, ColumnType<*>> = mutableMapOf(
        Int::class to IntColumnType,
        String::class to StringColumnType,
        Long::class to LongColumnType,
        Boolean::class to BooleanColumnType,
        Double::class to DoubleColumnType,
        Uuid::class to UUIDColumnType
    )

    inline fun <reified V : Any> baseColumnTypeFor(): ColumnType<V> {
        @Suppress("UNCHECKED_CAST")
        return registry.getValue(V::class) as ColumnType<V>
    }
}