package com.lhzkml.nowtest.route.search.scene

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
