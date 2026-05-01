package com.lhzkml.nowtest.core.network

import org.junit.Test
import kotlin.test.assertTrue

class NtNetworkDataSourceTest {
    @Test
    fun networkDataSourceDoesNotExposeRemovedContentApis() {
        val methodNames = NtNetworkDataSource::class.java.methods.map { it.name }

        assertTrue("getTopics" !in methodNames)
        assertTrue("getNewsResources" !in methodNames)
    }
}
