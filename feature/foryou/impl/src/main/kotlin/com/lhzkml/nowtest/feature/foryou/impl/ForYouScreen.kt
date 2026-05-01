package com.lhzkml.nowtest.feature.foryou.impl

import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import com.lhzkml.nowtest.core.designsystem.component.NtOverlayLoadingWheel
import com.lhzkml.nowtest.core.designsystem.component.scrollbar.DraggableScrollbar
import com.lhzkml.nowtest.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.lhzkml.nowtest.core.designsystem.component.scrollbar.scrollbarState
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.model.data.UserNewsResource
import com.lhzkml.nowtest.core.ui.DevicePreviews
import com.lhzkml.nowtest.core.ui.NewsFeedUiState
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent
import com.lhzkml.nowtest.core.ui.TrackScrollJank
import com.lhzkml.nowtest.core.ui.UserNewsResourcePreviewParameterProvider
import com.lhzkml.nowtest.core.ui.launchCustomChromeTab
import com.lhzkml.nowtest.core.ui.newsFeed

@Composable
fun ForYouScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ForYouViewModel = hiltViewModel(),
) {
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserNewsResource by viewModel.deepLinkedNewsResource.collectAsStateWithLifecycle()

    ForYouScreen(
        isSyncing = isSyncing,
        feedState = feedState,
        deepLinkedUserNewsResource = deepLinkedUserNewsResource,
        onDeepLinkOpened = viewModel::onDeepLinkOpened,
        onTopicClick = onTopicClick,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(it, true) },
        modifier = modifier,
    )
}

@Composable
internal fun ForYouScreen(
    isSyncing: Boolean,
    feedState: NewsFeedUiState,
    deepLinkedUserNewsResource: UserNewsResource?,
    onTopicClick: (String) -> Unit,
    onDeepLinkOpened: (String) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFeedLoading = feedState is NewsFeedUiState.Loading

    ReportDrawnWhen { !isSyncing && !isFeedLoading }

    val itemsAvailable = when (feedState) {
        NewsFeedUiState.Loading -> 0
        is NewsFeedUiState.Success -> feedState.feed.size
    }

    val state = rememberLazyStaggeredGridState()
    val scrollbarState = state.scrollbarState(
        itemsAvailable = itemsAvailable,
    )
    TrackScrollJank(scrollableState = state, stateName = "forYou:feed")

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 24.dp,
            modifier = Modifier
                .testTag("forYou:feed"),
            state = state,
        ) {
            newsFeed(
                feedState = feedState,
                onNewsResourceViewed = onNewsResourceViewed,
                onTopicClick = onTopicClick,
            )

            item(span = StaggeredGridItemSpan.FullLine, contentType = "bottomSpacing") {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
        AnimatedVisibility(
            visible = isSyncing || isFeedLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut(),
        ) {
            val loadingContentDescription = "Loading"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                NtOverlayLoadingWheel(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentDesc = loadingContentDescription,
                )
            }
        }
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
    TrackScreenViewEvent(screenName = "ForYou")
    NotificationPermissionEffect()
    DeepLinkEffect(
        deepLinkedUserNewsResource,
        onDeepLinkOpened,
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
private fun DeepLinkEffect(
    userNewsResource: UserNewsResource?,
    onDeepLinkOpened: (String) -> Unit,
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

    LaunchedEffect(userNewsResource) {
        if (userNewsResource == null) return@LaunchedEffect
        if (!userNewsResource.hasBeenViewed) onDeepLinkOpened(userNewsResource.id)

        launchCustomChromeTab(
            context = context,
            uri = Uri.parse(userNewsResource.url),
            toolbarColor = backgroundColor,
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedFeed(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NtTheme {
        ForYouScreen(
            isSyncing = false,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenLoading() {
    NtTheme {
        ForYouScreen(
            isSyncing = false,
            feedState = NewsFeedUiState.Loading,
            deepLinkedUserNewsResource = null,
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}

@DevicePreviews
@Composable
fun ForYouScreenPopulatedAndLoading(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    NtTheme {
        ForYouScreen(
            isSyncing = true,
            feedState = NewsFeedUiState.Success(
                feed = userNewsResources,
            ),
            deepLinkedUserNewsResource = null,
            onNewsResourceViewed = {},
            onTopicClick = {},
            onDeepLinkOpened = {},
        )
    }
}
