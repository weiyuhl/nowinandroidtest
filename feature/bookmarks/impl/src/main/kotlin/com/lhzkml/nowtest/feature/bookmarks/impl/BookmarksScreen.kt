package com.lhzkml.nowtest.feature.bookmarks.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent

@Composable
internal fun BookmarksScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
    TrackScreenViewEvent(screenName = "Saved")
}

@Preview
@Composable
private fun BookmarksScreenPreview() {
    NtTheme {
        BookmarksScreen()
    }
}
