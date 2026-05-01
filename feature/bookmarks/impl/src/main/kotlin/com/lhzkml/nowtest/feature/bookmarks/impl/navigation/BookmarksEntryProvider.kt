package com.lhzkml.nowtest.feature.bookmarks.impl.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.feature.bookmarks.api.navigation.BookmarksNavKey
import com.lhzkml.nowtest.feature.bookmarks.impl.BookmarksScreen

fun EntryProviderScope<NavKey>.bookmarksEntry() {
    entry<BookmarksNavKey> {
        BookmarksScreen()
    }
}

// TODO: Why is this here?
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState state should be initialized at runtime")
}
