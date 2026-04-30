package com.lhzkml.nowtest.core.datastore.test

import androidx.datastore.core.DataStore
import com.lhzkml.nowtest.core.datastore.UserPreferences
import com.lhzkml.nowtest.core.datastore.UserPreferencesSerializer
import com.lhzkml.nowtest.core.datastore.di.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
internal object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        serializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> = InMemoryDataStore(serializer.defaultValue)
}
