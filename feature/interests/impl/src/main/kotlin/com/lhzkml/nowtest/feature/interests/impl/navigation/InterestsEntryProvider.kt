package com.lhzkml.nowtest.feature.interests.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.feature.interests.api.navigation.InterestsNavKey
import com.lhzkml.nowtest.feature.interests.impl.InterestsScreen

fun EntryProviderScope<NavKey>.interestsEntry() {
    entry<InterestsNavKey> {
        InterestsScreen()
    }
}
