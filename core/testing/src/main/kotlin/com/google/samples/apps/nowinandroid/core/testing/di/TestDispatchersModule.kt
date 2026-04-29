package com.google.samples.apps.nowinandroid.core.testing.di

import com.google.samples.apps.nowinandroid.core.common.network.Dispatcher
import com.google.samples.apps.nowinandroid.core.common.network.NiaDispatchers.Default
import com.google.samples.apps.nowinandroid.core.common.network.NiaDispatchers.IO
import com.google.samples.apps.nowinandroid.core.common.network.di.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatchersModule::class],
)
internal object TestDispatchersModule {
    @Provides
    @Dispatcher(IO)
    fun providesIODispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @Dispatcher(Default)
    fun providesDefaultDispatcher(
        testDispatcher: TestDispatcher,
    ): CoroutineDispatcher = testDispatcher
}
