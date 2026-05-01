package com.lhzkml.nowtest.core.data.repository

import com.lhzkml.nowtest.core.model.data.UserNewsResource
import com.lhzkml.nowtest.core.model.data.mapToUserNewsResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements a [UserNewsResourceRepository] by combining a [NewsRepository] with a
 * [UserDataRepository].
 */
class CompositeUserNewsResourceRepository @Inject constructor(
    val newsRepository: NewsRepository,
    val userDataRepository: UserDataRepository,
) : UserNewsResourceRepository {

    /**
     * Returns available news resources (joined with user data) matching the given query.
     */
    override fun observeAll(
        query: NewsResourceQuery,
    ): Flow<List<UserNewsResource>> =
        newsRepository.getNewsResources(query)
            .combine(userDataRepository.userData) { newsResources, userData ->
                newsResources.mapToUserNewsResources(userData)
            }

    override fun observeAllBookmarked(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.bookmarkedNewsResources }.distinctUntilChanged()
            .flatMapLatest { bookmarkedNewsResources ->
                when {
                    bookmarkedNewsResources.isEmpty() -> flowOf(emptyList())
                    else -> observeAll(NewsResourceQuery(filterNewsIds = bookmarkedNewsResources))
                }
            }
}
