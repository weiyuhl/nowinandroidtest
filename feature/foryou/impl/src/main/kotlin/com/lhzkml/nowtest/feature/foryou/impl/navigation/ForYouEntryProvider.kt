package com.lhzkml.nowtest.feature.foryou.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.feature.foryou.api.navigation.ForYouNavKey
import com.lhzkml.nowtest.feature.foryou.impl.ForYouScreen

fun EntryProviderScope<NavKey>.forYouEntry() {
    entry<ForYouNavKey> {
        ForYouScreen()
    }
}
