package com.lhzkml.nowtest.feature.search.impl

import com.lhzkml.nowtest.core.model.data.Topic
import com.lhzkml.nowtest.core.model.data.UserNewsResource

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     */
    data object EmptyQuery : SearchResultUiState

    data object LoadFailed : SearchResultUiState

    data class Success(
        val topics: List<Topic> = emptyList(),
        val newsResources: List<UserNewsResource> = emptyList(),
    ) : SearchResultUiState {
        fun isEmpty(): Boolean = topics.isEmpty() && newsResources.isEmpty()
    }

    /**
     * A state where the search contents are not ready. This happens when the *Fts tables are not
     * populated yet.
     */
    data object SearchNotReady : SearchResultUiState
}
