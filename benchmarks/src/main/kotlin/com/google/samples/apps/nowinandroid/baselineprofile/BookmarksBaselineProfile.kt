package com.google.samples.apps.nowinandroid.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.google.samples.apps.nowinandroid.PACKAGE_NAME
import com.google.samples.apps.nowinandroid.bookmarks.goToBookmarksScreen
import com.google.samples.apps.nowinandroid.startActivityAndAllowNotifications
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
