package com.lhzkml.nowtest.core.database.di

import com.lhzkml.nowtest.core.database.NtDatabase
import com.lhzkml.nowtest.core.database.dao.NewsResourceDao
import com.lhzkml.nowtest.core.database.dao.TopicDao
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
}
