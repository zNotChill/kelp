package me.znotchill.kelp.dialects

object PostgresDialect : Dialect {
    override fun quoteIdentifier(name: String) = "\"$name\""
    override fun jsonType() = "JSONB"
    override fun existingColumnsSql(table: String) =
        "SELECT column_name AS name FROM information_schema.columns " +
                "WHERE table_schema = current_schema() AND table_name = '${table.lowercase()}';"
}