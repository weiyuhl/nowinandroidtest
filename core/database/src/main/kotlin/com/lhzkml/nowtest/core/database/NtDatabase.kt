package com.lhzkml.nowtest.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lhzkml.nowtest.core.database.model.DatabaseMetadataEntity
import com.lhzkml.nowtest.core.database.util.InstantConverter

@Database(
    entities = [
        DatabaseMetadataEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class NtDatabase : RoomDatabase()
