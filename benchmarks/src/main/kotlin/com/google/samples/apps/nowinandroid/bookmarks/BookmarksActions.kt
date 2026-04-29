package com.google.samples.apps.nowinandroid.bookmarks

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import com.google.samples.apps.nowinandroid.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToBookmarksScreen() {
    val savedSelector = By.text("Saved")
    val savedButton = device.findObject(savedSelector)
    savedButton.click()
    device.waitForIdle()
    // Wait until saved title are shown on screen
    waitForObjectOnTopAppBar(savedSelector)
}
