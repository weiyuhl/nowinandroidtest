package com.lhzkml.nowtest.route.test2.scene.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.route.test2.contract.navigation.Test2Route
import com.lhzkml.nowtest.route.test2.scene.Test2Screen

fun EntryProviderScope<NavKey>.test2Entry() {
    entry<Test2Route> {
        Test2Screen()
    }
}
