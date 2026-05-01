package com.lhzkml.nowtest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.lhzkml.nowtest.R
import com.lhzkml.nowtest.core.designsystem.component.NtBackground
import com.lhzkml.nowtest.core.designsystem.component.NtNavigationSuiteScaffold
import com.lhzkml.nowtest.core.designsystem.component.NtTopNavigationBar
import com.lhzkml.nowtest.core.designsystem.component.NtTopNavigationDestination
import com.lhzkml.nowtest.core.designsystem.icon.NtIcons
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.core.navigation.toEntries
import com.lhzkml.nowtest.feature.bookmarks.impl.navigation.bookmarksEntry
import com.lhzkml.nowtest.feature.foryou.impl.navigation.forYouEntry
import com.lhzkml.nowtest.feature.interests.impl.navigation.interestsEntry
import com.lhzkml.nowtest.feature.search.api.navigation.SearchNavKey
import com.lhzkml.nowtest.feature.search.impl.navigation.searchEntry
import com.lhzkml.nowtest.feature.settings.api.navigation.SettingsNavKey
import com.lhzkml.nowtest.feature.settings.impl.navigation.settingsEntry
import com.lhzkml.nowtest.navigation.TOP_LEVEL_NAV_ITEMS

internal val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState should be initialized at runtime")
}

@Composable
fun NtApp(
    appState: NtAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    NtBackground(modifier = modifier) {
        val snackbarHostState = remember { SnackbarHostState() }

        val isOffline by appState.isOffline.collectAsStateWithLifecycle()

        // If user is not connected to the internet show a snack bar to inform them.
        val notConnectedMessage = stringResource(R.string.not_connected)
        LaunchedEffect(isOffline) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            NtAppContent(
                appState = appState,
                windowAdaptiveInfo = windowAdaptiveInfo,
            )
        }
    }
}

@Composable
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
)
internal fun NtAppContent(
    appState: NtAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val snackbarHostState = LocalSnackbarHostState.current

    val navigator = remember { Navigator(appState.navigationState) }
    val navigationEventDispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)

    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides navigationEventDispatcherOwner) {
        NtNavigationSuiteScaffold(
            navigationSuiteItems = {
                TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                    val selected = navKey == appState.navigationState.currentTopLevelKey
                    item(
                        selected = selected,
                        onClick = { navigator.navigate(navKey) },
                        icon = {
                            Icon(
                                imageVector = navItem.unselectedIcon,
                                contentDescription = null,
                            )
                        },
                        selectedIcon = {
                            Icon(
                                imageVector = navItem.selectedIcon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(stringResource(navItem.iconTextId)) },
                        modifier = Modifier
                            .testTag("NtNavItem"),
                    )
                }
            },
            windowAdaptiveInfo = windowAdaptiveInfo,
        ) {
            Scaffold(
                modifier = modifier.semantics {
                    testTagsAsResourceId = true
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(
                        snackbarHostState,
                        modifier = Modifier.windowInsetsPadding(
                            WindowInsets.safeDrawing.exclude(
                                WindowInsets.ime,
                            ),
                        ),
                    )
                },
            ) { padding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        ),
                ) {
                    // Only show the top navigation bar on top level destinations.
                    var shouldShowTopNavigationBar = false

                    if (appState.navigationState.currentKey in appState.navigationState.topLevelKeys) {
                        shouldShowTopNavigationBar = true

                        val topNavigationDestinations = listOf<NtTopNavigationDestination<NavKey>>(
                            NtTopNavigationDestination(
                                key = SearchNavKey,
                                icon = NtIcons.Search,
                                contentDescription = stringResource(
                                    id = R.string.top_navigation_search_content_description,
                                ),
                            ),
                            NtTopNavigationDestination(
                                key = SettingsNavKey,
                                icon = NtIcons.Settings,
                                contentDescription = stringResource(
                                    id = R.string.top_navigation_settings_content_description,
                                ),
                            ),
                        )

                        NtTopNavigationBar(
                            destinations = topNavigationDestinations,
                            onNavigateToDestination = navigator::navigate,
                        )
                    }

                    Box(
                        // Workaround for https://issuetracker.google.com/338478720
                        modifier = Modifier.consumeWindowInsets(
                            if (shouldShowTopNavigationBar) {
                                WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                            } else {
                                WindowInsets(0, 0, 0, 0)
                            },
                        ),
                    ) {
                        val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

                        val entryProvider = entryProvider {
                            forYouEntry()
                            bookmarksEntry()
                            interestsEntry()
                            searchEntry(navigator)
                            settingsEntry(navigator)
                        }

                        NavDisplay(
                            entries = appState.navigationState.toEntries(entryProvider),
                            sceneStrategy = listDetailStrategy,
                            onBack = { navigator.goBack() },
                        )
                    }

                    // TODO: We may want to add padding or spacer when the snackbar is shown so that
                    //  content doesn't display behind it.
                }
            }
        }
    }
}
