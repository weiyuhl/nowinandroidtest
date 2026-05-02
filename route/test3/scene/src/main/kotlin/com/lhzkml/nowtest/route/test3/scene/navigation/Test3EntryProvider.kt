package com.lhzkml.nowtest.route.test3.scene.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.route.test3.contract.navigation.Test3Route
import com.lhzkml.nowtest.route.test3.scene.Test3Screen

fun EntryProviderScope<NavKey>.test3Entry() {
    entry<Test3Route> {
        Test3Screen()
    }
}
