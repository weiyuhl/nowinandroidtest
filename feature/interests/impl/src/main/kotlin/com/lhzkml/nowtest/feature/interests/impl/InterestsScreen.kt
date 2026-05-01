package com.lhzkml.nowtest.feature.interests.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhzkml.nowtest.core.designsystem.component.NtBackground
import com.lhzkml.nowtest.core.designsystem.component.NtLoadingWheel
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.model.data.Topic
import com.lhzkml.nowtest.core.ui.DevicePreviews
import com.lhzkml.nowtest.core.ui.TopicPreviewParameterProvider
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent
import com.lhzkml.nowtest.feature.interests.api.R

@Composable
fun InterestsScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterestsViewModel,
    shouldHighlightSelectedTopic: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InterestsScreen(
        uiState = uiState,
        onTopicClick = {
            viewModel.onTopicClick(it)
            onTopicClick(it)
        },
        shouldHighlightSelectedTopic = shouldHighlightSelectedTopic,
        modifier = modifier,
    )
}

@Composable
internal fun InterestsScreen(
    uiState: InterestsUiState,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedTopic: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            InterestsUiState.Loading ->
                NtLoadingWheel(
                    contentDesc = stringResource(id = R.string.feature_interests_api_loading),
                )

            is InterestsUiState.Interests ->
                TopicsTabContent(
                    topics = uiState.topics,
                    onTopicClick = onTopicClick,
                    selectedTopicId = uiState.selectedTopicId,
                    shouldHighlightSelectedTopic = shouldHighlightSelectedTopic,
                )

            is InterestsUiState.Empty -> InterestsEmptyScreen()
        }
    }
    TrackScreenViewEvent(screenName = "Interests")
}

@Composable
private fun InterestsEmptyScreen() {
    Text(text = stringResource(id = R.string.feature_interests_api_empty_header))
}

@DevicePreviews
@Composable
fun InterestsScreenPopulated(
    @PreviewParameter(TopicPreviewParameterProvider::class)
    topics: List<Topic>,
) {
    NtTheme {
        NtBackground {
            InterestsScreen(
                uiState = InterestsUiState.Interests(
                    selectedTopicId = null,
                    topics = topics,
                ),
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenLoading() {
    NtTheme {
        NtBackground {
            InterestsScreen(
                uiState = InterestsUiState.Loading,
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun InterestsScreenEmpty() {
    NtTheme {
        NtBackground {
            InterestsScreen(
                uiState = InterestsUiState.Empty,
                onTopicClick = {},
            )
        }
    }
}
