@file:Suppress("ktlint:standard:max-line-length")

package com.google.samples.apps.nowinandroid.feature.search.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.ui.PreviewParameterData.newsResources
import com.google.samples.apps.nowinandroid.core.ui.PreviewParameterData.topics

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [SearchResultUiState] for Composable previews.
 */
class SearchUiStatePreviewParameterProvider : PreviewParameterProvider<SearchResultUiState> {
    override val values: Sequence<SearchResultUiState> = sequenceOf(
        SearchResultUiState.Success(
            topics = topics.mapIndexed { i, topic ->
                FollowableTopic(topic = topic, isFollowed = i % 2 == 0)
            },
            newsResources = newsResources,
        ),
    )
}
