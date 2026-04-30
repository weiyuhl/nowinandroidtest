package com.lhzkml.nowtest.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.lhzkml.nowtest.PACKAGE_NAME
import com.lhzkml.nowtest.interests.goToInterestsScreen
import com.lhzkml.nowtest.interests.interestsScrollTopicsDownUp
import com.lhzkml.nowtest.startActivityAndAllowNotifications
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the "Interests" screen
 */
class InterestsBaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            // Navigate to interests screen
            goToInterestsScreen()
            interestsScrollTopicsDownUp()
        }
}
