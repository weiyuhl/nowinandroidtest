package com.lhzkml.nowtest.core.domain

import com.lhzkml.nowtest.core.data.repository.SearchContentsRepository
import com.lhzkml.nowtest.core.data.repository.UserDataRepository
import com.lhzkml.nowtest.core.model.data.FollowableTopic
import com.lhzkml.nowtest.core.model.data.SearchResult
import com.lhzkml.nowtest.core.model.data.UserData
import com.lhzkml.nowtest.core.model.data.UserNewsResource
import com.lhzkml.nowtest.core.model.data.UserSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which returns the searched contents matched with the search query.
 */
class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(
        searchQuery: String,
    ): Flow<UserSearchResult> =
        searchContentsRepository.searchContents(searchQuery)
            .mapToUserSearchResult(userDataRepository.userData)
}

private fun Flow<SearchResult>.mapToUserSearchResult(userDataStream: Flow<UserData>): Flow<UserSearchResult> =
    combine(userDataStream) { searchResult, userData ->
        UserSearchResult(
            topics = searchResult.topics.map { topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            },
            newsResources = searchResult.newsResources.map { news ->
                UserNewsResource(
                    newsResource = news,
                    userData = userData,
                )
            },
        )
    }
