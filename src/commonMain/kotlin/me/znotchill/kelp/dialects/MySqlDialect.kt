package me.znotchill.kelp.dialects

object MySqlDialect : Dialect {
    override fun quoteIdentifier(name: String) = "`$name`"
}