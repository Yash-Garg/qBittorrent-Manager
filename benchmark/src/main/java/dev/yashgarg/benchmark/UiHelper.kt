package dev.yashgarg.benchmark

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.navigateToConfigFragment() {
    val button = device.findObject(By.text("Add server"))
    button.click()

    device.waitForIdle()
    device.wait(Until.hasObject(By.text("Save and test config")), 1000)
}
