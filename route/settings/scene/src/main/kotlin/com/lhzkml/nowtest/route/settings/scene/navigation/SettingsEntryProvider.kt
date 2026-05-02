package com.lhzkml.nowtest.route.settings.scene.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.route.settings.contract.navigation.SettingsRoute
import com.lhzkml.nowtest.route.settings.scene.SettingsScreen

fun EntryProviderScope<NavKey>.settingsEntry(navigator: Navigator) {
    entry<SettingsRoute> {
        SettingsScreen(
            onBackClick = { navigator.goBack() },
        )
    }
}
