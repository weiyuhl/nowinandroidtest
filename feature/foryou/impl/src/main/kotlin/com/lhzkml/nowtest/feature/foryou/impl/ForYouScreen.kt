package com.lhzkml.nowtest.feature.foryou.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent

@Composable
fun ForYouScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
    TrackScreenViewEvent(screenName = "ForYou")
}

@Preview
@Composable
private fun ForYouScreenPreview() {
    NtTheme {
        ForYouScreen()
    }
}
