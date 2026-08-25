package me.znotchill.database.column

import me.znotchill.database.column.types.IntColumnType
import me.znotchill.database.column.types.StringColumnType
import kotlin.reflect.KClass

object ColumnTypes {
    val registry: MutableMap<KClass<*>, ColumnType<*>> = mutableMapOf(
        Int::class to IntColumnType,
        String::class to StringColumnType,
    )

    inline fun <reified V : Any> baseColumnTypeFor(): ColumnType<V> {
        @Suppress("UNCHECKED_CAST")
        return registry.getValue(V::class) as ColumnType<V>
    }
}