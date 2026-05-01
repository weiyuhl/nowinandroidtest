package com.lhzkml.nowtest.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.lhzkml.nowtest.PACKAGE_NAME
import com.lhzkml.nowtest.foryou.forYouScrollFeedDownUp
import com.lhzkml.nowtest.foryou.forYouWaitForContent
import com.lhzkml.nowtest.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "For You" screen
 */
class ForYouBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Scroll the feed critical user journey
            forYouWaitForContent()
            forYouScrollFeedDownUp()
        }
}
