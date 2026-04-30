package com.lhzkml.nowtest.core.sync.test

import com.lhzkml.nowtest.core.data.util.SyncManager
import com.lhzkml.nowtest.sync.di.SyncModule
import com.lhzkml.nowtest.sync.status.StubSyncSubscriber
import com.lhzkml.nowtest.sync.status.SyncSubscriber
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SyncModule::class],
)
internal interface TestSyncModule {
    @Binds
    fun bindsSyncStatusMonitor(
        syncStatusMonitor: NeverSyncingSyncManager,
    ): SyncManager

    @Binds
    fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
