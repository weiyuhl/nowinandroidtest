package com.lhzkml.nowtest.route.settings.scene.di

import com.lhzkml.nowtest.route.settings.scene.AndroidAppLanguageRepository
import com.lhzkml.nowtest.route.settings.scene.AppLanguageRepository
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
