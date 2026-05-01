package com.lhzkml.nowtest.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.navigation3.runtime.NavKey
import com.github.takahirom.roborazzi.captureRoboImage
import com.lhzkml.nowtest.core.designsystem.component.NtTopNavigationBar
import com.lhzkml.nowtest.core.designsystem.component.NtTopNavigationDestination
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import com.lhzkml.nowtest.core.testing.util.DefaultRoborazziOptions
import com.lhzkml.nowtest.core.testing.util.captureMultiTheme
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class TopNavigationBarScreenshotTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun topNavigationBar_multipleThemes() {
        composeTestRule.captureMultiTheme("TopNavigationBar") {
            NtTopNavigationBarExample()
        }
    }

    @Test
    fun topNavigationBar_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                DeviceConfigurationOverride(
                    DeviceConfigurationOverride.FontScale(2f),
                ) {
                    NtTheme {
                        NtTopNavigationBarExample()
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                "src/test/screenshots/TopNavigationBar/TopNavigationBar_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    @Composable
    private fun NtTopNavigationBarExample() {
        NtTopNavigationBar(
            destinations = listOf(
                NtTopNavigationDestination(
                    key = TestSearchNavKey,
                    icon = NtIcons.Search,
                    contentDescription = "Search",
                ),
                NtTopNavigationDestination(
                    key = TestSettingsNavKey,
                    icon = NtIcons.Settings,
                    contentDescription = "Settings",
                ),
            ),
            onNavigateToDestination = {},
        )
    }

    private object TestSearchNavKey : NavKey

    private object TestSettingsNavKey : NavKey
}
