package com.lhzkml.nowtest.route.test3.scene

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
fun Test3Screen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
    TrackScreenViewEvent(screenName = "Test3")
}

@DevicePreviews
@Composable
fun Test3ScreenPreview() {
    NtTheme {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxSize(),
        ) {
            Test3Screen()
        }
    }
}
