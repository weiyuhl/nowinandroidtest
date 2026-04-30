package com.lhzkml.nowtest.core.network

import com.lhzkml.nowtest.core.network.model.NetworkChangeList
import com.lhzkml.nowtest.core.network.model.NetworkNewsResource
import com.lhzkml.nowtest.core.network.model.NetworkTopic

/**
 * Interface representing network calls to the Nt backend
 */
interface NtNetworkDataSource {
    suspend fun getTopics(ids: List<String>? = null): List<NetworkTopic>

    suspend fun getNewsResources(ids: List<String>? = null): List<NetworkNewsResource>

    suspend fun getTopicChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getNewsResourceChangeList(after: Int? = null): List<NetworkChangeList>
}
