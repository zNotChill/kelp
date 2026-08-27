package me.znotchill.kelp.column

import me.znotchill.kelp.Database

open class Column<T>(
    val name: String,
    val type: ColumnType<T>,
    var nullable: Boolean = false,
    var primaryKey: Boolean = false
) {
    fun nullable(): Column<T?> {
        nullable = true

        @Suppress("UNCHECKED_CAST")
        return this as Column<T?>
    }

    fun primaryKey(): Column<T> {
        primaryKey = true
        return this
    }

    fun statement(db: Database): String = buildString {
        append(name)
        append(" ")
        append(type.sqlType(db.dialect))

        if (!nullable) {
            append(" NOT NULL")
        }

        if (primaryKey) {
            append(" PRIMARY KEY")
        }
    }
}