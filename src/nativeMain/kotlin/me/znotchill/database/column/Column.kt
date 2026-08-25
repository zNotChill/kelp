package me.znotchill.database.column

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

    fun statement(): String = buildString {
        append(name)
        append(" ")
        append(type.sqlType)

        if (!nullable) {
            append(" NOT NULL")
        }

        if (primaryKey) {
            append(" PRIMARY KEY")
        }
    }
}