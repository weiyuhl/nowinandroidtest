package com.lhzkml.nowtest.route.search.scene.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.core.navigation.Navigator
import com.lhzkml.nowtest.route.search.contract.navigation.SearchRoute
import com.lhzkml.nowtest.route.search.scene.SearchScreen

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchRoute> {
        SearchScreen(
            onBackClick = { navigator.goBack() },
        )
    }
}
