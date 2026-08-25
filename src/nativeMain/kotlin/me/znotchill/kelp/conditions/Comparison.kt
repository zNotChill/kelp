package me.znotchill.kelp.conditions

import me.znotchill.kelp.column.Column

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

class Not(val inner: Condition) : Condition() {
    override fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>> {
        val (sql, params) = inner.toSql(paramIndex)
        return "NOT $sql" to params
    }
}

fun not(condition: Condition): Condition = Not(condition)

infix fun <T> Column<T>.eq(value: T): Condition = Comparison(this, "=", value)
infix fun <T> Column<T>.gt(value: T): Condition = Comparison(this, ">", value)
infix fun <T> Column<T>.neq(value: T): Condition = Comparison(this, "!=", value)
infix fun <T> Column<T>.lt(value: T): Condition = Comparison(this, "<", value)
infix fun <T> Column<T>.lte(value: T): Condition = Comparison(this, "<=", value)
infix fun <T> Column<T>.gte(value: T): Condition = Comparison(this, ">=", value)
infix fun <T : Comparable<T>> Column<T>.between(range: ClosedRange<T>): Condition =
    Between(this, range.start, range.endInclusive)

infix fun <T : Comparable<T>> Column<T?>.between(range: ClosedRange<T>): Condition =
    Between(this, range.start, range.endInclusive)
infix fun Condition.and(other: Condition): Condition = And(this, other)
infix fun Condition.or(other: Condition): Condition = Or(this, other)

class IsNull(val column: Column<*>, val negate: Boolean = false) : Condition() {
    override fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>> {
        val sql = "${column.name} IS ${if (negate) "NOT NULL" else "NULL"}"
        return sql to emptyList()
    }
}

fun Column<*>.isNull(): Condition = IsNull(this, negate = false)
fun Column<*>.isNotNull(): Condition = IsNull(this, negate = true)

class Between<T : Comparable<T>>(
    val column: Column<*>,
    val low: T,
    val high: T
) : Condition() {
    override fun toSql(paramIndex: () -> Int): Pair<String, List<Any?>> {
        val lowIdx = paramIndex()
        val highIdx = paramIndex()
        val sql = "${column.name} BETWEEN :p$lowIdx AND :p$highIdx"
        return sql to listOf(low, high)
    }
}