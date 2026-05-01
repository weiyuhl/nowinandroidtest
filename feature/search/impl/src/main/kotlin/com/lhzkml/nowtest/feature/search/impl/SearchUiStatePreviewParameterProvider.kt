package com.lhzkml.nowtest.feature.search.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Provides list of [SearchResultUiState] for Composable previews.
 */
class SearchUiStatePreviewParameterProvider : PreviewParameterProvider<SearchResultUiState> {
    override val values: Sequence<SearchResultUiState> = sequenceOf(
        SearchResultUiState.Success(
            results = localSearchTestContents,
        ),
    )
}
