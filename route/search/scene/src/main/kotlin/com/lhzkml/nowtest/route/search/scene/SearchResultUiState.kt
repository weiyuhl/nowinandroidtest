package com.lhzkml.nowtest.route.search.scene

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     */
    data object EmptyQuery : SearchResultUiState

    data class Success(
        val results: List<SearchTestContent> = emptyList(),
    ) : SearchResultUiState {
        fun isEmpty(): Boolean = results.isEmpty()
    }
}
