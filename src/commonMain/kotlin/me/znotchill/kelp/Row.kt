package me.znotchill.kelp

import me.znotchill.kelp.column.Column

open class Row(
    private val values: Map<String, Any?>
) {
    operator fun <T> get(column: Column<T>): T {
        return column.type.fromDatabase(values[column.name])
    }
}