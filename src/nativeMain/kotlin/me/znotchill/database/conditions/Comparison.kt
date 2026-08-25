package me.znotchill.database.conditions

import me.znotchill.database.column.Column

sealed class Condition {
    abstract fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>>
}

fun Condition.render(): Pair<String, List<Any?>> {
    var counter = 0
    val paramIndex = { counter++ }
    return toSql(paramIndex)
}

class Comparison(
    val column: Column<*>,
    val op: String,
    val value: Any?
) : Condition() {
    override fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>> {
        val index = paramIndex()
        val placeholder = "p$index"
        val sql = "${column.name} $op :$placeholder"
        return sql to listOf(value)
    }
}

class And(val left: Condition, val right: Condition) : Condition() {
    override fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>> {
        val (leftSql, leftParams) = left.toSql(paramIndex)
        val (rightSql, rightParams) = right.toSql(paramIndex)
        return "($leftSql AND $rightSql)" to (leftParams + rightParams)
    }
}

class Or(val left: Condition, val right: Condition) : Condition() {
    override fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>> {
        val (leftSql, leftParams) = left.toSql(paramIndex)
        val (rightSql, rightParams) = right.toSql(paramIndex)
        return "($leftSql OR $rightSql)" to (leftParams + rightParams)
    }
}

infix fun <T> Column<T>.eq(value: T): Condition = Comparison(this, "=", value)