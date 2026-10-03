package me.znotchill.kelp.dialects

object SqliteDialect : Dialect {
    override fun quoteIdentifier(name: String) = "\"$name\""
    override fun jsonType() = "TEXT"
}