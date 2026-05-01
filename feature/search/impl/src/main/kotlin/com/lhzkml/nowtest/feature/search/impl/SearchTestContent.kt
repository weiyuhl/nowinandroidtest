package com.lhzkml.nowtest.feature.search.impl

data class SearchTestContent(
    val id: String,
    val title: String,
    val description: String,
)

internal val localSearchTestContents = listOf(
    SearchTestContent(
        id = "test-content-1234",
        title = "1234 test content",
        description = "Local search test item containing 1234.",
    ),
    SearchTestContent(
        id = "test-content-compose",
        title = "Compose test content",
        description = "Local search test item for Compose.",
    ),
)

internal fun searchLocalTestContents(query: String): List<SearchTestContent> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.length < SEARCH_TEST_QUERY_MIN_LENGTH) return emptyList()

    return localSearchTestContents.filter { content ->
        content.id.contains(normalizedQuery, ignoreCase = true) ||
            content.title.contains(normalizedQuery, ignoreCase = true) ||
            content.description.contains(normalizedQuery, ignoreCase = true)
    }
}

private const val SEARCH_TEST_QUERY_MIN_LENGTH = 2
