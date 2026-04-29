package com.google.samples.apps.nowinandroid.sync.di

import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.sync.status.FirebaseSyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.SyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    @Binds
    internal abstract fun bindsSyncSubscriber(
        syncSubscriber: FirebaseSyncSubscriber,
    ): SyncSubscriber

    companion object {
        @Provides
        @Singleton
        internal fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging
    }
}
