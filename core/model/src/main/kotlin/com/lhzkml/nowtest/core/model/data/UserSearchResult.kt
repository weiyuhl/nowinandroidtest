package com.lhzkml.nowtest.core.model.data

/**
 * An entity of [SearchResult] with additional user information.
 */
data class UserSearchResult(
    val topics: List<Topic> = emptyList(),
    val newsResources: List<UserNewsResource> = emptyList(),
)
