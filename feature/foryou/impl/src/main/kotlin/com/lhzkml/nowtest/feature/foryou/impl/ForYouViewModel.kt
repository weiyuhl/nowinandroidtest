package com.lhzkml.nowtest.feature.foryou.impl

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lhzkml.nowtest.core.analytics.AnalyticsEvent
import com.lhzkml.nowtest.core.analytics.AnalyticsEvent.Param
import com.lhzkml.nowtest.core.analytics.AnalyticsHelper
import com.lhzkml.nowtest.core.data.repository.NewsResourceQuery
import com.lhzkml.nowtest.core.data.repository.UserDataRepository
import com.lhzkml.nowtest.core.data.repository.UserNewsResourceRepository
import com.lhzkml.nowtest.core.data.util.SyncManager
import com.lhzkml.nowtest.core.notifications.DEEP_LINK_NEWS_RESOURCE_ID_KEY
import com.lhzkml.nowtest.core.ui.NewsFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    private val analyticsHelper: AnalyticsHelper,
    private val userDataRepository: UserDataRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
) : ViewModel() {

    val deepLinkedNewsResource = savedStateHandle.getStateFlow<String?>(
        key = DEEP_LINK_NEWS_RESOURCE_ID_KEY,
        null,
    )
        .flatMapLatest { newsResourceId ->
            if (newsResourceId == null) {
                flowOf(emptyList())
            } else {
                userNewsResourceRepository.observeAll(
                    NewsResourceQuery(
                        filterNewsIds = setOf(newsResourceId),
                    ),
                )
            }
        }
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val feedState: StateFlow<NewsFeedUiState> =
        userNewsResourceRepository.observeAll()
            .map(NewsFeedUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NewsFeedUiState.Loading,
            )

    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(newsResourceId, viewed)
        }
    }

    fun onDeepLinkOpened(newsResourceId: String) {
        if (newsResourceId == deepLinkedNewsResource.value?.id) {
            savedStateHandle[DEEP_LINK_NEWS_RESOURCE_ID_KEY] = null
        }
        analyticsHelper.logNewsDeepLinkOpen(newsResourceId = newsResourceId)
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = true,
            )
        }
    }
}

private fun AnalyticsHelper.logNewsDeepLinkOpen(newsResourceId: String) =
    logEvent(
        AnalyticsEvent(
            type = "news_deep_link_opened",
            extras = listOf(
                Param(
                    key = DEEP_LINK_NEWS_RESOURCE_ID_KEY,
                    value = newsResourceId,
                ),
            ),
        ),
    )
