package me.znotchill.database

import me.znotchill.database.column.Column

open class Row(
    private val values: Map<String, Any?>
) {
    operator fun <T> get(column: Column<T>): T {
        return column.type.fromDatabase(values[column.name])
    }
}