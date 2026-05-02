package com.lhzkml.nowtest.ui

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import com.lhzkml.nowtest.MainActivity
import com.lhzkml.nowtest.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.lhzkml.nowtest.core.ui.R as CoreUiR
import com.lhzkml.nowtest.route.settings.scene.R as SettingsR
import com.lhzkml.nowtest.route.test1.contract.R as Test1R
import com.lhzkml.nowtest.route.test2.contract.R as Test2R
import com.lhzkml.nowtest.route.test3.contract.R as Test3R

/**
 * Tests all the navigation flows that are handled by the navigation library.
 */
@HiltAndroidTest
class NavigationTest {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // The strings used for matching in these tests
    private val navigateUp by composeTestRule.stringResource(CoreUiR.string.core_ui_back)
    private val test1 by composeTestRule.stringResource(Test1R.string.route_test1_contract_title)
    private val test3 by composeTestRule.stringResource(Test3R.string.route_test3_contract_title)
    private val test2 by composeTestRule.stringResource(Test2R.string.route_test2_contract_title)
    private val search by composeTestRule.stringResource(R.string.top_navigation_search_content_description)
    private val settings by composeTestRule.stringResource(R.string.top_navigation_settings_content_description)
    private val darkModePreference by composeTestRule.stringResource(
        SettingsR.string.route_settings_scene_dark_mode_preference,
    )

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun firstScreen_isTest1() {
        composeTestRule.apply {
            // VERIFY test 1 is selected
            onNodeWithText(test1).assertIsSelected()
        }
    }

    /*
     * Top level destinations should never show an up affordance.
     */
    @Test
    fun topLevelDestinations_doNotShowUpArrow() {
        composeTestRule.apply {
            // GIVEN the user is on any of the top level destinations, THEN the Up arrow is not shown.
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()

            onNodeWithText(test2).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()

            onNodeWithText(test3).performClick()
            onNodeWithContentDescription(navigateUp).assertDoesNotExist()
        }
    }

    @Test
    fun topLevelDestinations_showTopNavigationEntries() {
        composeTestRule.apply {
            onNodeWithContentDescription(search).assertExists()
            onNodeWithContentDescription(settings).assertExists()

            onNodeWithText(test2).performClick()
            onNodeWithContentDescription(search).assertExists()
            onNodeWithContentDescription(settings).assertExists()

            onNodeWithText(test3).performClick()
            onNodeWithContentDescription(search).assertExists()
            onNodeWithContentDescription(settings).assertExists()
        }
    }

    @Test
    fun whenSearchIconIsClicked_searchScreenIsShown() {
        composeTestRule.apply {
            onNodeWithContentDescription(search).performClick()

            onNodeWithTag("searchTextField").assertExists()
        }
    }

    @Test
    fun whenSettingsIconIsClicked_settingsScreenIsShown() {
        composeTestRule.apply {
            onNodeWithContentDescription(settings).performClick()

            // Check that one of the settings is actually displayed.
            onNodeWithText(darkModePreference).assertExists()
        }
    }

    @Test
    fun whenSettingsScreenBackPressed_previousScreenIsDisplayed() {
        composeTestRule.apply {
            // Navigate to the test 2 screen, open the settings screen, then go back.
            onNodeWithText(test2).performClick()
            onNodeWithContentDescription(settings).performClick()
            Espresso.pressBack()

            // Check that the test 2 screen is still visible and selected.
            onNode(hasText(test2) and hasTestTag("NtNavItem")).assertIsSelected()
        }
    }

    /*
     * There should always be at most one instance of a top-level destination at the same time.
     */
    @Test(expected = NoActivityResumedException::class)
    fun homeDestination_back_quitsApp() {
        composeTestRule.apply {
            // GIVEN the user navigates to the Test3 destination
            onNodeWithText(test3).performClick()
            // and then navigates to the Test 1 destination
            onNodeWithText(test1).performClick()
            // WHEN the user uses the system button/gesture to go back
            Espresso.pressBack()
            // THEN the app quits
        }
    }

    /*
     * When pressing back from any top level destination except "Test 1", the app navigates back
     * to the "Test 1" destination, no matter which destinations you visited in between.
     */
    @Test
    fun navigationBar_backFromAnyDestination_returnsToTest1() {
        composeTestRule.apply {
            // GIVEN the user navigated to the Test3 destination
            onNodeWithText(test3).performClick()
            // TODO: Add another destination here to increase test coverage, see b/226357686.
            // WHEN the user uses the system button/gesture to go back,
            Espresso.pressBack()
            // THEN the app shows the Test 1 destination
            onNodeWithText(test1).assertExists()
        }
    }
}
