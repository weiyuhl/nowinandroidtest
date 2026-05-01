plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.android.library.compose)
    alias(libs.plugins.nowtest.android.library.jacoco)
}

android {
    namespace = "com.lhzkml.nowtest.core.ui"
}

dependencies {
    api(libs.androidx.metrics)
    api(projects.core.analytics)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
