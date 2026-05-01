package com.lhzkml.nowtest.core.model.data

import kotlinx.datetime.Instant

/**
 * A [NewsResource] with additional user information such as whether they have saved (bookmarked)
 * this news resource.
 */
data class UserNewsResource internal constructor(
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    val headerImageUrl: String?,
    val publishDate: Instant,
    val type: String,
    val topics: List<Topic>,
    val isSaved: Boolean,
    val hasBeenViewed: Boolean,
) {
    constructor(newsResource: NewsResource, userData: UserData) : this(
        id = newsResource.id,
        title = newsResource.title,
        content = newsResource.content,
        url = newsResource.url,
        headerImageUrl = newsResource.headerImageUrl,
        publishDate = newsResource.publishDate,
        type = newsResource.type,
        topics = newsResource.topics,
        isSaved = newsResource.id in userData.bookmarkedNewsResources,
        hasBeenViewed = newsResource.id in userData.viewedNewsResources,
    )
}

fun List<NewsResource>.mapToUserNewsResources(userData: UserData): List<UserNewsResource> =
    map { UserNewsResource(it, userData) }
