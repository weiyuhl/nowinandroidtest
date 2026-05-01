package com.lhzkml.nowtest.feature.topic.impl

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhzkml.nowtest.core.designsystem.component.DynamicAsyncImage
import com.lhzkml.nowtest.core.designsystem.component.NtBackground
import com.lhzkml.nowtest.core.designsystem.component.NtLoadingWheel
import com.lhzkml.nowtest.core.designsystem.component.scrollbar.DraggableScrollbar
import com.lhzkml.nowtest.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.lhzkml.nowtest.core.designsystem.component.scrollbar.scrollbarState
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.model.data.Topic
import com.lhzkml.nowtest.core.model.data.UserNewsResource
import com.lhzkml.nowtest.core.ui.DevicePreviews
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent
import com.lhzkml.nowtest.core.ui.TrackScrollJank
import com.lhzkml.nowtest.core.ui.UserNewsResourcePreviewParameterProvider
import com.lhzkml.nowtest.core.ui.userNewsResourceCardItems
import com.lhzkml.nowtest.core.ui.R as UiR
import com.lhzkml.nowtest.feature.topic.api.R as TopicR

@Composable
fun TopicScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopicViewModel = hiltViewModel(),
) {
    val topicUiState: TopicUiState by viewModel.topicUiState.collectAsStateWithLifecycle()
    val newsUiState: NewsUiState by viewModel.newsUiState.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = "Topic: ${viewModel.topicId}")
    TopicScreen(
        topicUiState = topicUiState,
        newsUiState = newsUiState,
        modifier = modifier.testTag("topic:${viewModel.topicId}"),
        showBackButton = showBackButton,
        onBackClick = onBackClick,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
        onTopicClick = onTopicClick,
    )
}

@VisibleForTesting
@Composable
internal fun TopicScreen(
    topicUiState: TopicUiState,
    newsUiState: NewsUiState,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "topic:screen")
    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            }
            when (topicUiState) {
                TopicUiState.Loading -> item {
                    NtLoadingWheel(
                        modifier = modifier,
                        contentDesc = stringResource(id = TopicR.string.feature_topic_api_loading),
                    )
                }

                TopicUiState.Error -> TODO()
                is TopicUiState.Success -> {
                    item {
                        TopicToolbar(
                            showBackButton = showBackButton,
                            onBackClick = onBackClick,
                            topic = topicUiState.topic,
                        )
                    }
                    topicBody(
                        name = topicUiState.topic.name,
                        description = topicUiState.topic.longDescription,
                        news = newsUiState,
                        imageUrl = topicUiState.topic.imageUrl,
                        onNewsResourceViewed = onNewsResourceViewed,
                        onTopicClick = onTopicClick,
                    )
                }
            }
            item {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
        val itemsAvailable = topicItemsSize(topicUiState, newsUiState)
        val scrollbarState = state.scrollbarState(
            itemsAvailable = itemsAvailable,
        )
        state.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = state.rememberDraggableScroller(
                itemsAvailable = itemsAvailable,
            ),
        )
    }
}

private fun topicItemsSize(
    topicUiState: TopicUiState,
    newsUiState: NewsUiState,
) = when (topicUiState) {
    TopicUiState.Error -> 0 // Nothing
    TopicUiState.Loading -> 1 // Loading bar
    is TopicUiState.Success -> when (newsUiState) {
        NewsUiState.Error -> 0 // Nothing
        NewsUiState.Loading -> 1 // Loading bar
        is NewsUiState.Success -> 2 + newsUiState.news.size // Toolbar, header
    }
}

private fun LazyListScope.topicBody(
    name: String,
    description: String,
    news: NewsUiState,
    imageUrl: String,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    // TODO: Show icon if available
    item {
        TopicHeader(name, description, imageUrl)
    }

    userNewsResourceCards(news, onNewsResourceViewed, onTopicClick)
}

@Composable
private fun TopicHeader(name: String, description: String, imageUrl: String) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
    ) {
        DynamicAsyncImage(
            imageUrl = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(132.dp)
                .padding(bottom = 12.dp),
        )
        Text(name, style = MaterialTheme.typography.displayMedium)
        if (description.isNotEmpty()) {
            Text(
                description,
                modifier = Modifier.padding(top = 24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

// TODO: Could/should this be replaced with [LazyGridScope.newsFeed]?
private fun LazyListScope.userNewsResourceCards(
    news: NewsUiState,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    when (news) {
        is NewsUiState.Success -> {
            userNewsResourceCardItems(
                items = news.news,
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
                itemModifier = Modifier.padding(24.dp),
            )
        }

        is NewsUiState.Loading -> item {
            NtLoadingWheel(contentDesc = "Loading news") // TODO
        }

        else -> item {
            Text("Error") // TODO
        }
    }
}

@Preview
@Composable
private fun TopicBodyPreview() {
    NtTheme {
        LazyColumn {
            topicBody(
                name = "Jetpack Compose",
                description = "Lorem ipsum maximum",
                news = NewsUiState.Success(emptyList()),
                imageUrl = "",
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

@Composable
private fun TopicToolbar(
    topic: Topic,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
    ) {
        if (showBackButton) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = NtIcons.ArrowBack,
                    contentDescription = stringResource(
                        id = UiR.string.core_ui_back,
                    ),
                )
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }
    }
}

@DevicePreviews
@Composable
fun TopicScreenPopulated(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NtTheme {
        NtBackground {
            TopicScreen(
                topicUiState = TopicUiState.Success(userNewsResources[0].topics[0]),
                newsUiState = NewsUiState.Success(userNewsResources),
                showBackButton = true,
                onBackClick = {},
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
fun TopicScreenLoading() {
    NtTheme {
        NtBackground {
            TopicScreen(
                topicUiState = TopicUiState.Loading,
                newsUiState = NewsUiState.Loading,
                showBackButton = true,
                onBackClick = {},
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
