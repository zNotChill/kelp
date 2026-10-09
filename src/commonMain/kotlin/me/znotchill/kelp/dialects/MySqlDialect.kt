package me.znotchill.kelp.dialects

object MySqlDialect : Dialect {
    override fun quoteIdentifier(name: String) = "`$name`"
    override fun jsonType() = "JSON"
    override fun existingColumnsSql(table: String) =
        "SELECT column_name AS name FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() AND table_name = '$table';"
}