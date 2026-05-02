package com.lhzkml.nowtest.route.search.scene

import com.lhzkml.nowtest.route.search.contract.R as searchR

data class SearchTestContent(
    val id: String,
    val titleResId: Int,
    val descriptionResId: Int,
    val searchTerms: List<String>,
)

internal val localSearchTestContents = listOf(
    SearchTestContent(
        id = "test-content-1234",
        titleResId = searchR.string.route_search_contract_test_content_1234_title,
        descriptionResId = searchR.string.route_search_contract_test_content_1234_description,
        searchTerms = listOf("1234", "test content"),
    ),
    SearchTestContent(
        id = "test-content-compose",
        titleResId = searchR.string.route_search_contract_test_content_compose_title,
        descriptionResId = searchR.string.route_search_contract_test_content_compose_description,
        searchTerms = listOf("compose", "test content"),
    ),
)

internal fun searchLocalTestContents(query: String): List<SearchTestContent> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.length < SEARCH_TEST_QUERY_MIN_LENGTH) return emptyList()

    return localSearchTestContents.filter { content ->
        content.id.contains(normalizedQuery, ignoreCase = true) ||
            content.searchTerms.any { term ->
                term.contains(normalizedQuery, ignoreCase = true)
            }
    }
}

private const val SEARCH_TEST_QUERY_MIN_LENGTH = 2
