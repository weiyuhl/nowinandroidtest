package com.lhzkml.nowtest.feature.interests.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.ui.DevicePreviews
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent

@Composable
fun InterestsScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
    TrackScreenViewEvent(screenName = "Interests")
}

@DevicePreviews
@Composable
fun InterestsScreenPreview() {
    NtTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize(),
        ) {
            InterestsScreen()
        }
    }
}
