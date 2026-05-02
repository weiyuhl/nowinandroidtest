package com.lhzkml.nowtest.route.test1.scene.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.lhzkml.nowtest.route.test1.contract.navigation.Test1Route
import com.lhzkml.nowtest.route.test1.scene.Test1Screen

fun EntryProviderScope<NavKey>.test1Entry() {
    entry<Test1Route> {
        Test1Screen()
    }
}
