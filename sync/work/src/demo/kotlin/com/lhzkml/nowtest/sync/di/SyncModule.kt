package com.lhzkml.nowtest.sync.di

import com.lhzkml.nowtest.core.data.util.SyncManager
import com.lhzkml.nowtest.sync.status.StubSyncSubscriber
import com.lhzkml.nowtest.sync.status.SyncSubscriber
import com.lhzkml.nowtest.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    @Binds
    internal abstract fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
