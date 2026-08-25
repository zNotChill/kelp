package me.znotchill.kelp

import io.github.smyrgeorge.sqlx4k.Statement
import me.znotchill.kelp.column.Column
import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.column.ColumnTypes
import me.znotchill.kelp.column.nullable
import me.znotchill.kelp.conditions.Condition
import me.znotchill.kelp.conditions.render
import me.znotchill.kelp.exceptions.InvalidTableNameException

open class Model<T>(
    val tableName: String,
) {
    val columns: MutableList<Column<*>> = mutableListOf()
    private val extractors: MutableMap<String, (T) -> Any?> = mutableMapOf()

    init {
        if (tableName.any { !it.isLetterOrDigit() && it != '_' })
            throw InvalidTableNameException("Invalid table name '$tableName'")
    }

    open fun decode(row: Row): T {
        throw NotImplementedError("Decode method not implemented for this model")
    }

    fun <V> registerColumn(
        name: String,
        type: ColumnType<V>,
        nullable: Boolean,
        extractor: (T) -> V
    ): Column<V> {
        require(columns.none { it.name == name }) {
            "Column '$name' already registered on table '$tableName'"
        }
        val column = Column(name, type, nullable = nullable)
        columns.add(column)
        extractors[name] = { extractor(it) }
        return column
    }

    fun insertStatement(data: T): Statement {
        val columnString = columns.map {
            it.name
        }.joinToString { it }
        val valueString = columns.map {
            ":col_${it.name}"
        }.joinToString { it }

        var statement = Statement.create(
            "INSERT INTO $tableName ($columnString) VALUES ($valueString);"
        )

        for (column in columns) {
            @Suppress("UNCHECKED_CAST")
            val typedColumn = column as Column<Any?>
            val rawValue = extractors.getValue(column.name)(data)
            val dbValue = typedColumn.type.toDatabase(rawValue)
            statement = statement.bind("col_${column.name}", dbValue)
        }

        return statement
    }

    suspend fun <M : Model<T>, T> M.where(
        db: Database,
        builder: M.() -> Condition
    ): List<T> {
        val condition = this.builder()
        val (whereSql, params) = condition.render()

        var statement = Statement.create("SELECT * FROM $tableName WHERE $whereSql;")
        params.forEachIndexed { i, value ->
            statement = statement.bind("p$i", value)
        }
        println(statement.sql)

        val rows = db.driver.fetchAll(statement).getOrThrow()
        val rowMap = rows.rows.map { row ->
            val entries = mutableMapOf<String, Any?>()
            columns.forEach { col ->
                val colValue = row.get(col.name)
                entries[col.name] = col.type.fromDatabase(colValue.asString())
            }
            decode(Row(entries))
        }
        return rowMap
    }

    suspend fun insert(db: Database, data: T) {
        db.driver.execute(insertStatement(data)).getOrThrow()
    }

    inline fun <reified V : Any> Model<T>.column(
        name: String,
        noinline extractor: (T) -> V
    ): Column<V> {
        val type = ColumnTypes.baseColumnTypeFor<V>()
        return registerColumn(name, type, nullable = false, extractor)
    }

    inline fun <reified V : Any> Model<T>.nullable(
        name: String,
        noinline extractor: (T) -> V?
    ): Column<V?> {
        val type = ColumnTypes.baseColumnTypeFor<V>().nullable()
        return registerColumn(name, type, nullable = true, extractor)
    }

    fun createStatement(): String = buildString {
        append("CREATE TABLE ")
        append(tableName)
        append(" (")

        columns.joinTo(this, separator = ", ") { column ->
            column.statement()
        }

        append(")")
    }

    fun dropStatement() = buildString {
        append("DROP TABLE ")
        append(tableName)
        append(";")
    }
}