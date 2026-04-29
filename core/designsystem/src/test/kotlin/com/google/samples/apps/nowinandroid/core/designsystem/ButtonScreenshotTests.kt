package com.google.samples.apps.nowinandroid.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOutlinedButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.testing.util.captureMultiTheme
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
class ButtonScreenshotTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun niaButton_multipleThemes() {
        composeTestRule.captureMultiTheme("Button") { description ->
            Surface {
                NiaButton(onClick = {}, text = { Text("$description Button") })
            }
        }
    }

    @Test
    fun niaOutlineButton_multipleThemes() {
        composeTestRule.captureMultiTheme("Button", "OutlineButton") { description ->
            Surface {
                NiaOutlinedButton(onClick = {}, text = { Text("$description OutlineButton") })
            }
        }
    }

    @Test
    fun niaButton_leadingIcon_multipleThemes() {
        composeTestRule.captureMultiTheme(
            name = "Button",
            overrideFileName = "ButtonLeadingIcon",
            shouldCompareAndroidTheme = false,
        ) { description ->
            Surface {
                NiaButton(
                    onClick = {},
                    text = { Text("$description Icon Button") },
                    leadingIcon = { Icon(imageVector = NiaIcons.Add, contentDescription = null) },
                )
            }
        }
    }
}
