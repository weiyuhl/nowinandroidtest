package com.lhzkml.nowtest.feature.settings.impl.di

import com.lhzkml.nowtest.feature.settings.impl.AndroidAppLanguageRepository
import com.lhzkml.nowtest.feature.settings.impl.AppLanguageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface AppLanguageModule {
    @Binds
    fun bindsAppLanguageRepository(
        repository: AndroidAppLanguageRepository,
    ): AppLanguageRepository
}
