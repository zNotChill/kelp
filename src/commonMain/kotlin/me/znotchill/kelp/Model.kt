package me.znotchill.kelp

import io.github.smyrgeorge.sqlx4k.ResultSet
import io.github.smyrgeorge.sqlx4k.Statement
import kotlinx.serialization.KSerializer
import me.znotchill.kelp.column.Column
import me.znotchill.kelp.column.ColumnType
import me.znotchill.kelp.column.ColumnTypes
import me.znotchill.kelp.column.nullable
import me.znotchill.kelp.column.types.EnumColumnType
import me.znotchill.kelp.column.types.ListColumnType
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
        val column = Column(this, name, type, nullable = nullable)
        columns.add(column)
        extractors[name] = { extractor(it) }
        return column
    }

    fun insertStatement(
        db: Database,
        data: T
    ): Statement {
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
            val dbValue = typedColumn.type.toDatabase(rawValue, db.dialect)
            statement = statement.bind("col_${column.name}", dbValue)
        }

        return statement
    }

    suspend fun <M : Model<T>, T> M.where(
        db: Database,
        orderBy: Column<*>? = null,
        descending: Boolean = false,
        offset: Long = 0,
        limit: Long? = null,
        builder: M.() -> Condition
    ): List<T> {
        val condition = this.builder()
        val (whereSql, params) = condition.render()

        var sql = "SELECT * FROM $tableName WHERE $whereSql"

        orderBy?.let {
            sql += " ORDER BY ${it.name} ${if (descending) "DESC" else "ASC"}"
        }

        if (offset > 0) {
            sql += " OFFSET $offset"
        }

        limit?.let {
            sql += " LIMIT $it"
        }

        sql += ";"

        var statement = Statement.create(sql)

        params.forEachIndexed { i, value ->
            statement = statement.bind("p$i", value)
        }

        val rows = db.driver.fetchAll(statement).getOrThrow()

        return rows.rows.map { row ->
            decodeRow(row, db)
        }
    }

    suspend fun insert(db: Database, data: T) {
        db.driver.execute(insertStatement(db, data)).getOrThrow()
    }

    suspend fun <M : Model<T>, T> M.update(
        db: Database,
        data: T,
        builder: M.() -> Condition
    ) {
        val condition = builder()
        val (whereSql, params) = condition.render()

        val assignments = columns.joinToString(", ") {
            "${it.name} = :col_${it.name}"
        }

        var statement = Statement.create(
            "UPDATE $tableName SET $assignments WHERE $whereSql;"
        )

        for (column in columns) {
            @Suppress("UNCHECKED_CAST")
            val typedColumn = column as Column<Any?>
            val rawValue = extractors.getValue(column.name)(data)
            val dbValue = typedColumn.type.toDatabase(rawValue, db.dialect)
            statement = statement.bind("col_${column.name}", dbValue)
        }

        params.forEachIndexed { i, value ->
            statement = statement.bind("p$i", value)
        }

        db.driver.execute(statement).getOrThrow()
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

    inline fun <reified V : Any> Model<T>.list(
        name: String,
        noinline extractor: (T) -> List<V>
    ): Column<List<V>> {
        val type = ListColumnType(ColumnTypes.baseColumnTypeFor<V>())
        return registerColumn(name, type, nullable = false, extractor)
    }

    inline fun <reified E> enum(
        name: String,
        serializer: KSerializer<E>,
        noinline extractor: (T) -> E
    ): Column<E> where E : Enum<E> {
        return registerColumn(
            name,
            EnumColumnType(serializer, enumValues<E>()),
            nullable = false,
            extractor
        )
    }

    suspend fun recent(
        db: Database,
        column: Column<*>,
        limit: Number
    ): List<T> {
        require(limit.toInt() > 0) { "limit must be greater than 0" }

        val statement = Statement.create(
            "SELECT * FROM $tableName ORDER BY ${column.name} DESC LIMIT $limit;"
        )

        val rows = db.driver.fetchAll(statement).getOrThrow()

        return rows.rows.map { row ->
            decodeRow(row, db)
        }
    }

    suspend fun getAll(db: Database): List<T> {
        val statement = Statement.create("SELECT * FROM $tableName;")
        val rows = db.driver.fetchAll(statement).getOrThrow()

        return rows.rows.map { row ->
            decodeRow(row, db)
        }
    }

    fun decodeRow(
        row: ResultSet.Row,
        db: Database
    ): T {
        val entries = mutableMapOf<String, Any?>()

        columns.forEach { column ->
            entries[column.name] = row.get(column.name).asStringOrNull()
        }

        return decode(Row(entries, db))
    }

    fun createStatement(db: Database): String = buildString {
        append("CREATE TABLE ")
        append(tableName)
        append(" (")

        columns.joinTo(this, separator = ", ") { column ->
            column.statement(db)
        }

        append(")")
    }

    fun dropStatement() = buildString {
        append("DROP TABLE ")
        append(tableName)
        append(";")
    }
}