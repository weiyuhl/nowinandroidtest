package com.lhzkml.nowtest.test1

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import com.lhzkml.nowtest.waitForObjectOnTopNavigationBar

fun MacrobenchmarkScope.waitForTest1Content() {
    waitForObjectOnTopNavigationBar(By.desc("Search"))
}

fun MacrobenchmarkScope.setAppTheme(isDark: Boolean) {
    when (isDark) {
        true -> device.findObject(By.text("Dark")).click()
        false -> device.findObject(By.text("Light")).click()
    }
    device.waitForIdle()
    device.findObject(By.text("OK")).click()

    // Wait until the top navigation bar is visible on screen
    waitForTest1Content()
}
