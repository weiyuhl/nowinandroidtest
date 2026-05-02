package com.lhzkml.nowtest.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import com.lhzkml.nowtest.PACKAGE_NAME
import com.lhzkml.nowtest.startActivityAndAllowNotifications
import com.lhzkml.nowtest.test1.waitForTest1Content
import org.junit.Rule
import org.junit.Test

/**
 * Baseline Profile of the Test 1 screen.
 */
class Test1BaselineProfile {
    @get:Rule val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() =
        baselineProfileRule.collect(PACKAGE_NAME) {
            startActivityAndAllowNotifications()

            waitForTest1Content()
        }
}
