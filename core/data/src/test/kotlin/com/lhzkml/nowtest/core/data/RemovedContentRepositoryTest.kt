package com.lhzkml.nowtest.core.data

import org.junit.Test
import kotlin.test.assertFailsWith

class RemovedContentRepositoryTest {
    @Test
    fun removedContentRepositoriesAreNotPresent() {
        assertFailsWith<ClassNotFoundException> {
            Class.forName("com.lhzkml.nowtest.core.data.repository.TopicsRepository")
        }
        assertFailsWith<ClassNotFoundException> {
            Class.forName("com.lhzkml.nowtest.core.data.repository.NewsRepository")
        }
    }
}
