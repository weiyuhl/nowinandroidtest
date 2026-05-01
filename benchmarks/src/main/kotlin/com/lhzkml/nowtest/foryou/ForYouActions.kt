package com.lhzkml.nowtest.foryou

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import com.lhzkml.nowtest.flingElementDownUp
import com.lhzkml.nowtest.waitAndFindObject
import com.lhzkml.nowtest.waitForObjectOnTopNavigationBar

fun MacrobenchmarkScope.forYouWaitForContent() {
    device.wait(Until.gone(By.res("loadingWheel")), 5_000)
    val obj = device.waitAndFindObject(By.res("forYou:feed"), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    obj.wait(untilHasChildren(), 60_000)
}

fun MacrobenchmarkScope.forYouScrollFeedDownUp() {
    val feedList = device.findObject(By.res("forYou:feed"))
    device.flingElementDownUp(feedList)
}

fun MacrobenchmarkScope.setAppTheme(isDark: Boolean) {
    when (isDark) {
        true -> device.findObject(By.text("Dark")).click()
        false -> device.findObject(By.text("Light")).click()
    }
    device.waitForIdle()
    device.findObject(By.text("OK")).click()

    // Wait until the top navigation bar is visible on screen
    waitForObjectOnTopNavigationBar(By.desc("Search"))
}
