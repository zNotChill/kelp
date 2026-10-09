package me.znotchill.kelp.migration

data class MigrationResult(
    val created: Boolean,
    val addedColumns: List<String>,
    val extraColumns: List<String>,
)