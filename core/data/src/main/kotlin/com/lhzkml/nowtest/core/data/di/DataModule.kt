package com.lhzkml.nowtest.core.data.di

import com.lhzkml.nowtest.core.data.repository.DefaultRecentSearchRepository
import com.lhzkml.nowtest.core.data.repository.DefaultSearchContentsRepository
import com.lhzkml.nowtest.core.data.repository.NewsRepository
import com.lhzkml.nowtest.core.data.repository.OfflineFirstNewsRepository
import com.lhzkml.nowtest.core.data.repository.OfflineFirstTopicsRepository
import com.lhzkml.nowtest.core.data.repository.OfflineFirstUserDataRepository
import com.lhzkml.nowtest.core.data.repository.RecentSearchRepository
import com.lhzkml.nowtest.core.data.repository.SearchContentsRepository
import com.lhzkml.nowtest.core.data.repository.TopicsRepository
import com.lhzkml.nowtest.core.data.repository.UserDataRepository
import com.lhzkml.nowtest.core.data.util.ConnectivityManagerNetworkMonitor
import com.lhzkml.nowtest.core.data.util.NetworkMonitor
import com.lhzkml.nowtest.core.data.util.TimeZoneBroadcastMonitor
import com.lhzkml.nowtest.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository

    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    @Binds
    internal abstract fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
