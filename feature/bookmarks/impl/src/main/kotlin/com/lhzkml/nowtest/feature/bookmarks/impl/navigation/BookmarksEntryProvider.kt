package com.lhzkml.nowtest.feature.bookmarks.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.feature.bookmarks.api.navigation.BookmarksNavKey
import com.lhzkml.nowtest.feature.bookmarks.impl.BookmarksScreen

fun EntryProviderScope<NavKey>.bookmarksEntry() {
    entry<BookmarksNavKey> {
        BookmarksScreen()
    }
}
