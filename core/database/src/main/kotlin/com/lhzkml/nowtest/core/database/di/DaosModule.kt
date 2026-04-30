package com.lhzkml.nowtest.core.database.di

import com.lhzkml.nowtest.core.database.NtDatabase
import com.lhzkml.nowtest.core.database.dao.NewsResourceDao
import com.lhzkml.nowtest.core.database.dao.NewsResourceFtsDao
import com.lhzkml.nowtest.core.database.dao.RecentSearchQueryDao
import com.lhzkml.nowtest.core.database.dao.TopicDao
import com.lhzkml.nowtest.core.database.dao.TopicFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTopicsDao(
        database: NtDatabase,
    ): TopicDao = database.topicDao()

    @Provides
    fun providesNewsResourceDao(
        database: NtDatabase,
    ): NewsResourceDao = database.newsResourceDao()

    @Provides
    fun providesTopicFtsDao(
        database: NtDatabase,
    ): TopicFtsDao = database.topicFtsDao()

    @Provides
    fun providesNewsResourceFtsDao(
        database: NtDatabase,
    ): NewsResourceFtsDao = database.newsResourceFtsDao()

    @Provides
    fun providesRecentSearchQueryDao(
        database: NtDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()
}
