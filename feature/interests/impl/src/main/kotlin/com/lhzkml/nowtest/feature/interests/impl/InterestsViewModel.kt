package com.lhzkml.nowtest.feature.interests.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhzkml.nowtest.core.domain.GetTopicsUseCase
import com.lhzkml.nowtest.core.domain.TopicSortField
import com.lhzkml.nowtest.core.model.data.Topic
import com.lhzkml.nowtest.feature.interests.api.navigation.InterestsNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = InterestsViewModel.Factory::class)
class InterestsViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    getTopics: GetTopicsUseCase,
    @Assisted val key: InterestsNavKey,
) : ViewModel() {

    private val selectedTopicIdKey = "selectedTopicIdKey"

    private val selectedTopicId = savedStateHandle.getStateFlow(
        key = selectedTopicIdKey,
        initialValue = key.initialTopicId,
    )

    val uiState: StateFlow<InterestsUiState> = combine(
        selectedTopicId,
        getTopics(sortBy = TopicSortField.NAME),
        InterestsUiState::Interests,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InterestsUiState.Loading,
    )

    fun onTopicClick(topicId: String?) {
        savedStateHandle[selectedTopicIdKey] = topicId
    }

    @AssistedFactory
    interface Factory {
        fun create(key: InterestsNavKey): InterestsViewModel
    }
}

sealed interface InterestsUiState {
    data object Loading : InterestsUiState

    data class Interests(
        val selectedTopicId: String?,
        val topics: List<Topic>,
    ) : InterestsUiState

    data object Empty : InterestsUiState
}
