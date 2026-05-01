package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.model.data.UserNewsResource
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [UserNewsResource]
 */
interface UserNewsResourceRepository {
    /**
     * Returns available news resources as a stream.
     */
    fun observeAll(
        query: NewsResourceQuery = NewsResourceQuery(
            filterTopicIds = null,
            filterNewsIds = null,
        ),
    ): Flow<List<UserNewsResource>>
}
