package me.znotchill.kelp.dialects

object PostgresDialect : Dialect {
    override fun quoteIdentifier(name: String) = "\"$name\""
}