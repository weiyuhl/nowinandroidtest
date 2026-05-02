package com.lhzkml.nowtest.route.settings.scene

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.DARK
import com.lhzkml.nowtest.route.settings.scene.SettingsUiState.Loading
import com.lhzkml.nowtest.route.settings.scene.SettingsUiState.Success
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int) = composeTestRule.activity.resources.getString(id)

    @Test
    fun whenLoading_showsLoadingText() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Loading,
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule
            .onNodeWithText(getString(R.string.route_settings_scene_loading))
            .assertExists()
    }

    @Test
    fun whenStateIsSuccess_allDefaultSettingsAreDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                    ),
                ),
                onChangeDarkThemeConfig = {},
            )
        }

        // Check that all the possible settings are displayed.
        composeTestRule.onNodeWithText(
            getString(R.string.route_settings_scene_dark_mode_config_system_default),
        ).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_dark_mode_config_light)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_dark_mode_config_dark)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_language_preference)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_language_system_default)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_language_english)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_language_simplified_chinese)).assertExists()

        // Check that the correct settings are selected.
        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_dark_mode_config_dark)).assertIsSelected()
    }

    @Test
    fun whenStateIsSuccess_selectedLanguageIsDisplayedAndCanBeChanged() {
        var changedLanguage: AppLanguage? = null

        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                        appLanguage = AppLanguage.ENGLISH,
                    ),
                ),
                onChangeDarkThemeConfig = {},
                onChangeLanguage = { changedLanguage = it },
            )
        }

        composeTestRule
            .onNodeWithText(getString(R.string.route_settings_scene_language_english))
            .assertIsSelected()

        composeTestRule
            .onNodeWithText(getString(R.string.route_settings_scene_language_simplified_chinese))
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(AppLanguage.SIMPLIFIED_CHINESE, changedLanguage)
        }
    }

    @Test
    fun whenStateIsSuccess_darkModePreferenceCanBeChanged() {
        var changedDarkThemeConfig = DARK

        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                    ),
                ),
                onChangeDarkThemeConfig = { changedDarkThemeConfig = it },
            )
        }

        composeTestRule
            .onNodeWithText(getString(R.string.route_settings_scene_dark_mode_config_system_default))
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(com.lhzkml.nowtest.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM, changedDarkThemeConfig)
        }
    }

    @Test
    fun whenStateIsSuccess_licensesLinkIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                    ),
                ),
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.route_settings_scene_licenses)).assertExists()
    }
}
