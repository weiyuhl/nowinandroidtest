plugins {
    alias(libs.plugins.nowinandroid.android.feature.impl)
    alias(libs.plugins.nowinandroid.android.library.compose)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.feature.foryou.impl"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(projects.core.domain)
    implementation(projects.core.notifications)
    implementation(projects.feature.foryou.api)
    implementation(projects.feature.topic.api)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(projects.core.testing)
    testDemoImplementation(projects.core.screenshotTesting)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
