package me.znotchill.kelp.dialects

object SqliteDialect : Dialect {
    override fun quoteIdentifier(name: String) = "\"$name\""
    override fun jsonType() = "TEXT"
    override fun existingColumnsSql(table: String) =
        "SELECT name FROM pragma_table_info('$table');"
}