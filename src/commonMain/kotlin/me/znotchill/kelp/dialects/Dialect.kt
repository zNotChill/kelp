package me.znotchill.kelp.dialects

interface Dialect {
    fun quoteIdentifier(name: String): String
    fun jsonType(): String
}