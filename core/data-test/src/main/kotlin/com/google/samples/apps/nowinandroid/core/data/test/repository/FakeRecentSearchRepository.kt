package com.google.samples.apps.nowinandroid.core.data.test.repository

import com.google.samples.apps.nowinandroid.core.data.model.RecentSearchQuery
import com.google.samples.apps.nowinandroid.core.data.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Fake implementation of the [RecentSearchRepository]
 */
internal class FakeRecentSearchRepository @Inject constructor() : RecentSearchRepository {
    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) = Unit

    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        flowOf(emptyList())

    override suspend fun clearRecentSearches() = Unit
}
