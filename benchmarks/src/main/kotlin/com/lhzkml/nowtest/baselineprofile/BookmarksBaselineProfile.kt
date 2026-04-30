package com.lhzkml.nowtest.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.lhzkml.nowtest.PACKAGE_NAME
import com.lhzkml.nowtest.bookmarks.goToBookmarksScreen
import com.lhzkml.nowtest.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "Bookmarks" screen
 */
class BookmarksBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Navigate to saved screen
            goToBookmarksScreen()
        }
}
