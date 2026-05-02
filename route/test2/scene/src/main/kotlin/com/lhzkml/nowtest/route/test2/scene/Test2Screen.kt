package com.lhzkml.nowtest.route.test2.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.ui.TrackScreenViewEvent

@Composable
internal fun Test2Screen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize())
    TrackScreenViewEvent(screenName = "Test 2")
}

@Preview
@Composable
private fun Test2ScreenPreview() {
    NtTheme {
        Test2Screen()
    }
}
