package com.lhzkml.nowtest.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation3.runtime.NavBackStack
import com.lhzkml.nowtest.core.navigation.NavigationState
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.core.testing.util.TestNetworkMonitor
import com.lhzkml.nowtest.core.testing.util.TestTimeZoneMonitor
import com.lhzkml.nowtest.route.test1.contract.navigation.Test1Route
import com.lhzkml.nowtest.route.test2.contract.navigation.Test2Route
import com.lhzkml.nowtest.route.test3.contract.navigation.Test3Route
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

    // Subject under test.
    private lateinit var state: NtAppState

    private fun testNavigationState() = NavigationState(
        startKey = Test1Route,
        topLevelStack = NavBackStack(Test1Route),
        subStacks = mapOf(
            Test1Route to NavBackStack(Test1Route),
            Test2Route to NavBackStack(Test2Route),
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
                    timeZoneMonitor = timeZoneMonitor,
                    navigationState = navigationState,
                )
            }
        }

        assertEquals(Test1Route, state.navigationState.currentTopLevelKey)
        assertEquals(Test1Route, state.navigationState.currentKey)

        // Navigate to another destination once
        navigator.navigate(Test2Route)

        composeTestRule.waitForIdle()

        assertEquals(Test2Route, state.navigationState.currentTopLevelKey)
        assertEquals(Test2Route, state.navigationState.currentKey)
    }

    @Test
    fun ntAppState_destinations() = runTest {
        composeTestRule.setContent {
            state = rememberNtAppState(
                networkMonitor = networkMonitor,
                timeZoneMonitor = timeZoneMonitor,
            )
        }

        val navigationState = state.navigationState

        assertEquals(3, navigationState.topLevelKeys.size)
        assertEquals(
            setOf(Test1Route, Test2Route, Test3Route),
            navigationState.topLevelKeys,
        )
    }

    @Test
    fun ntAppState_whenNetworkMonitorIsOffline_StateIsOffline() = runTest(UnconfinedTestDispatcher()) {
        composeTestRule.setContent {
            state = NtAppState(
                coroutineScope = backgroundScope,
                networkMonitor = networkMonitor,
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
