plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.android.library.compose)
    alias(libs.plugins.nowtest.hilt)
}

android {
    namespace = "com.lhzkml.nowtest.core.screenshottesting"
}

dependencies {
    api(libs.bundles.androidx.compose.ui.test)
    api(libs.roborazzi)
    api(libs.roborazzi.accessibility.check)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.activity.compose)
    implementation(libs.robolectric)
    implementation(projects.core.designsystem)
}
