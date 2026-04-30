package com.lhzkml.nowtest.core.database.di

import android.content.Context
import androidx.room.Room
import com.lhzkml.nowtest.core.database.NtDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesNtDatabase(
        @ApplicationContext context: Context,
    ): NtDatabase = Room.databaseBuilder(
        context,
        NtDatabase::class.java,
        "nt-database",
    ).build()
}
