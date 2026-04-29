package com.google.samples.apps.nowinandroid.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {

    private lateinit var db: NiaDatabase
    protected lateinit var newsResourceDao: NewsResourceDao
    protected lateinit var topicDao: TopicDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                NiaDatabase::class.java,
            ).build()
        }
        newsResourceDao = db.newsResourceDao()
        topicDao = db.topicDao()
    }

    @After
    fun teardown() = db.close()
}
