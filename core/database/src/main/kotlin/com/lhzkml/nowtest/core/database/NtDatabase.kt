package com.lhzkml.nowtest.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lhzkml.nowtest.core.database.dao.NewsResourceDao
import com.lhzkml.nowtest.core.database.dao.TopicDao
import com.lhzkml.nowtest.core.database.model.NewsResourceEntity
import com.lhzkml.nowtest.core.database.model.NewsResourceTopicCrossRef
import com.lhzkml.nowtest.core.database.model.TopicEntity
import com.lhzkml.nowtest.core.database.util.InstantConverter

@Database(
    entities = [
        NewsResourceEntity::class,
        NewsResourceTopicCrossRef::class,
        TopicEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class NtDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun newsResourceDao(): NewsResourceDao
}
