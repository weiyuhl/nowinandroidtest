package com.lhzkml.nowtest.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Keeps the Room database component valid after feature-owned tables are removed.
 */
@Entity(tableName = "database_metadata")
data class DatabaseMetadataEntity(
    @PrimaryKey
    val id: String,
)
