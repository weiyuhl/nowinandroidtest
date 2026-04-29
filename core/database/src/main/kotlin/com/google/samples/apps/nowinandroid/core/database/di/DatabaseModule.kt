package com.google.samples.apps.nowinandroid.core.database.di

import android.content.Context
import androidx.room.Room
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
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
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): NiaDatabase = Room.databaseBuilder(
        context,
        NiaDatabase::class.java,
        "nia-database",
    ).build()
}
