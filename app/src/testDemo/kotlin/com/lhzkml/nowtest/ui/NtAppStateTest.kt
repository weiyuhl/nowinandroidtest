package com.lhzkml.nowtest.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation3.runtime.NavBackStack
import com.lhzkml.nowtest.core.data.repository.CompositeUserNewsResourceRepository
import com.lhzkml.nowtest.core.navigation.NavigationState
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.core.testing.repository.TestNewsRepository
import com.lhzkml.nowtest.core.testing.repository.TestUserDataRepository
import com.lhzkml.nowtest.core.testing.util.TestNetworkMonitor
import com.lhzkml.nowtest.core.testing.util.TestTimeZoneMonitor
import com.lhzkml.nowtest.feature.bookmarks.api.navigation.BookmarksNavKey
import com.lhzkml.nowtest.feature.foryou.api.navigation.ForYouNavKey
import com.lhzkml.nowtest.feature.interests.api.navigation.InterestsNavKey
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/**
 * Tests [NtAppState].
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class NtAppStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create the test dependencies.
    private val networkMonitor = TestNetworkMonitor()

    private val timeZoneMonitor = TestTimeZoneMonitor()

    private val userNewsResourceRepository =
        CompositeUserNewsResourceRepository(TestNewsRepository(), TestUserDataRepository())

    // Subject under test.
    private lateinit var state: NtAppState

    private fun testNavigationState() = NavigationState(
        startKey = ForYouNavKey,
        topLevelStack = NavBackStack(ForYouNavKey),
        subStacks = mapOf(
            ForYouNavKey to NavBackStack(ForYouNavKey),
            BookmarksNavKey to NavBackStack(BookmarksNavKey),
        ),
    )

    @Test
    fun ntAppState_currentDestination() = runTest {
        val navigationState = testNavigationState()
        val navigator = Navigator(navigationState)

        composeTestRule.setContent {
            state = remember(navigationState) {
                NtAppState(
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    userNewsResourceRepository = userNewsResourceRepository,
                    timeZoneMonitor = timeZoneMonitor,
                    navigationState = navigationState,
                )
            }
        }

        assertEquals(ForYouNavKey, state.navigationState.currentTopLevelKey)
        assertEquals(ForYouNavKey, state.navigationState.currentKey)

        // Navigate to another destination once
        navigator.navigate(BookmarksNavKey)

        composeTestRule.waitForIdle()

        assertEquals(BookmarksNavKey, state.navigationState.currentTopLevelKey)
        assertEquals(BookmarksNavKey, state.navigationState.currentKey)
    }

    @Test
    fun ntAppState_destinations() = runTest {
        composeTestRule.setContent {
            state = rememberNtAppState(
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )
        }

        val navigationState = state.navigationState

        assertEquals(3, navigationState.topLevelKeys.size)
        assertEquals(
            setOf(ForYouNavKey, BookmarksNavKey, InterestsNavKey),
            navigationState.topLevelKeys,
        )
    }

    @Test
    fun ntAppState_whenNetworkMonitorIsOffline_StateIsOffline() = runTest(UnconfinedTestDispatcher()) {
        composeTestRule.setContent {
            state = NtAppState(
                coroutineScope = backgroundScope,
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
                navigationState = testNavigationState(),
            )
        }

        backgroundScope.launch { state.isOffline.collect() }
        networkMonitor.setConnected(false)
        assertEquals(
            true,
            state.isOffline.value,
        )
    }

    @Test
    fun ntAppState_differentTZ_withTimeZoneMonitorChange() = runTest(UnconfinedTestDispatcher()) {
        composeTestRule.setContent {
            state = NtAppState(
                coroutineScope = backgroundScope,
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
                navigationState = testNavigationState(),
            )
        }
        val changedTz = TimeZone.of("Europe/Prague")
        backgroundScope.launch { state.currentTimeZone.collect() }
        timeZoneMonitor.setTimeZone(changedTz)
        assertEquals(
            changedTz,
            state.currentTimeZone.value,
        )
    }
}
