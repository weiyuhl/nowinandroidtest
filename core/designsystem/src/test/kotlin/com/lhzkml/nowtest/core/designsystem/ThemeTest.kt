package com.lhzkml.nowtest.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import com.lhzkml.nowtest.core.designsystem.theme.DarkDefaultColorScheme
import com.lhzkml.nowtest.core.designsystem.theme.LightDefaultColorScheme
import com.lhzkml.nowtest.core.designsystem.theme.NtTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * Tests [NtTheme] using the supported light and dark theme modes.
 */
@RunWith(RobolectricTestRunner::class)
class ThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun darkThemeFalse_usesLightDefaultTheme() {
        composeTestRule.setContent {
            NtTheme(darkTheme = false) {
                assertColorSchemesEqual(LightDefaultColorScheme, MaterialTheme.colorScheme)
            }
        }
    }

    @Test
    fun darkThemeTrue_usesDarkDefaultTheme() {
        composeTestRule.setContent {
            NtTheme(darkTheme = true) {
                assertColorSchemesEqual(DarkDefaultColorScheme, MaterialTheme.colorScheme)
            }
        }
    }

    private fun assertColorSchemesEqual(
        expectedColorScheme: ColorScheme,
        actualColorScheme: ColorScheme,
    ) {
        assertEquals(expectedColorScheme.primary, actualColorScheme.primary)
        assertEquals(expectedColorScheme.onPrimary, actualColorScheme.onPrimary)
        assertEquals(expectedColorScheme.primaryContainer, actualColorScheme.primaryContainer)
        assertEquals(expectedColorScheme.onPrimaryContainer, actualColorScheme.onPrimaryContainer)
        assertEquals(expectedColorScheme.secondary, actualColorScheme.secondary)
        assertEquals(expectedColorScheme.onSecondary, actualColorScheme.onSecondary)
        assertEquals(expectedColorScheme.secondaryContainer, actualColorScheme.secondaryContainer)
        assertEquals(
            expectedColorScheme.onSecondaryContainer,
            actualColorScheme.onSecondaryContainer,
        )
        assertEquals(expectedColorScheme.tertiary, actualColorScheme.tertiary)
        assertEquals(expectedColorScheme.onTertiary, actualColorScheme.onTertiary)
        assertEquals(expectedColorScheme.tertiaryContainer, actualColorScheme.tertiaryContainer)
        assertEquals(expectedColorScheme.onTertiaryContainer, actualColorScheme.onTertiaryContainer)
        assertEquals(expectedColorScheme.error, actualColorScheme.error)
        assertEquals(expectedColorScheme.onError, actualColorScheme.onError)
        assertEquals(expectedColorScheme.errorContainer, actualColorScheme.errorContainer)
        assertEquals(expectedColorScheme.onErrorContainer, actualColorScheme.onErrorContainer)
        assertEquals(expectedColorScheme.background, actualColorScheme.background)
        assertEquals(expectedColorScheme.onBackground, actualColorScheme.onBackground)
        assertEquals(expectedColorScheme.surface, actualColorScheme.surface)
        assertEquals(expectedColorScheme.onSurface, actualColorScheme.onSurface)
        assertEquals(expectedColorScheme.surfaceVariant, actualColorScheme.surfaceVariant)
        assertEquals(expectedColorScheme.onSurfaceVariant, actualColorScheme.onSurfaceVariant)
        assertEquals(expectedColorScheme.outline, actualColorScheme.outline)
        assertEquals(expectedColorScheme.outlineVariant, actualColorScheme.outlineVariant)
        assertEquals(expectedColorScheme.scrim, actualColorScheme.scrim)
        assertEquals(expectedColorScheme.inverseSurface, actualColorScheme.inverseSurface)
        assertEquals(expectedColorScheme.inverseOnSurface, actualColorScheme.inverseOnSurface)
        assertEquals(expectedColorScheme.inversePrimary, actualColorScheme.inversePrimary)
        assertEquals(expectedColorScheme.surfaceDim, actualColorScheme.surfaceDim)
        assertEquals(expectedColorScheme.surfaceBright, actualColorScheme.surfaceBright)
        assertEquals(
            expectedColorScheme.surfaceContainerLowest,
            actualColorScheme.surfaceContainerLowest,
        )
        assertEquals(expectedColorScheme.surfaceContainerLow, actualColorScheme.surfaceContainerLow)
        assertEquals(expectedColorScheme.surfaceContainer, actualColorScheme.surfaceContainer)
        assertEquals(expectedColorScheme.surfaceContainerHigh, actualColorScheme.surfaceContainerHigh)
        assertEquals(
            expectedColorScheme.surfaceContainerHighest,
            actualColorScheme.surfaceContainerHighest,
        )
    }
}
