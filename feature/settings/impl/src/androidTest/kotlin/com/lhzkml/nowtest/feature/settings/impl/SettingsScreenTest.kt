package com.lhzkml.nowtest.feature.settings.impl

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lhzkml.nowtest.core.model.data.DarkThemeConfig.DARK
import com.lhzkml.nowtest.core.model.data.ThemeBrand.ANDROID
import com.lhzkml.nowtest.core.model.data.ThemeBrand.DEFAULT
import com.lhzkml.nowtest.feature.settings.impl.SettingsUiState.Loading
import com.lhzkml.nowtest.feature.settings.impl.SettingsUiState.Success
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
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule
            .onNodeWithText(getString(R.string.feature_settings_impl_loading))
            .assertExists()
    }

    @Test
    fun whenStateIsSuccess_allDefaultSettingsAreDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        brand = ANDROID,
                        useDynamicColor = false,
                        darkThemeConfig = DARK,
                    ),
                ),
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        // Check that all the possible settings are displayed.
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_brand_default)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_brand_android)).assertExists()
        composeTestRule.onNodeWithText(
            getString(R.string.feature_settings_impl_dark_mode_config_system_default),
        ).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dark_mode_config_light)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dark_mode_config_dark)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_language_preference)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_language_system_default)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_language_english)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_language_simplified_chinese)).assertExists()

        // Check that the correct settings are selected.
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_brand_android)).assertIsSelected()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dark_mode_config_dark)).assertIsSelected()
    }

    @Test
    fun whenStateIsSuccess_selectedLanguageIsDisplayedAndCanBeChanged() {
        var changedLanguage: AppLanguage? = null

        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                        appLanguage = AppLanguage.ENGLISH,
                    ),
                ),
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
                onChangeLanguage = { changedLanguage = it },
            )
        }

        composeTestRule
            .onNodeWithText(getString(R.string.feature_settings_impl_language_english))
            .assertIsSelected()

        composeTestRule
            .onNodeWithText(getString(R.string.feature_settings_impl_language_simplified_chinese))
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(AppLanguage.SIMPLIFIED_CHINESE, changedLanguage)
        }
    }

    @Test
    fun whenStateIsSuccess_supportsDynamicColor_usesDefaultBrand_DynamicColorOptionIsDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        brand = DEFAULT,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                supportDynamicColor = true,
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_preference)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_yes)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_no)).assertExists()

        // Check that the correct default dynamic color setting is selected.
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_no)).assertIsSelected()
    }

    @Test
    fun whenStateIsSuccess_notSupportDynamicColor_DynamicColorOptionIsNotDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_preference))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_yes)).assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_no)).assertDoesNotExist()
    }

    @Test
    fun whenStateIsSuccess_usesAndroidBrand_DynamicColorOptionIsNotDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_preference))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_yes)).assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_dynamic_color_no)).assertDoesNotExist()
    }

    @Test
    fun whenStateIsSuccess_allLinksAreDisplayed() {
        composeTestRule.setContent {
            SettingsScreen(
                settingsUiState = Success(
                    UserEditableSettings(
                        brand = ANDROID,
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onChangeDynamicColorPreference = {},
                onChangeThemeBrand = {},
                onChangeDarkThemeConfig = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_privacy_policy)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_licenses)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_brand_guidelines)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feature_settings_impl_feedback)).assertExists()
    }
}
