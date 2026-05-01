package com.lhzkml.nowtest.core.domain

import com.lhzkml.nowtest.core.data.repository.SearchContentsRepository
import com.lhzkml.nowtest.core.model.data.UserNewsResource
import com.lhzkml.nowtest.core.model.data.UserSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * A use case which returns the searched contents matched with the search query.
 */
class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository,
) {

    operator fun invoke(
        searchQuery: String,
    ): Flow<UserSearchResult> =
        searchContentsRepository.searchContents(searchQuery)
            .map { searchResult ->
                UserSearchResult(
                    topics = searchResult.topics,
                    newsResources = searchResult.newsResources.map { news ->
                        UserNewsResource(
                            newsResource = news,
                            userData = com.lhzkml.nowtest.core.model.data.UserData(
                                viewedNewsResources = emptySet(),
                                themeBrand = com.lhzkml.nowtest.core.model.data.ThemeBrand.DEFAULT,
                                darkThemeConfig = com.lhzkml.nowtest.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM,
                                useDynamicColor = false,
                            ),
                        )
                    },
                )
            }
}
