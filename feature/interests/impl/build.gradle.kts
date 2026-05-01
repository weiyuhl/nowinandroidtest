plugins {
    alias(libs.plugins.nowtest.android.feature.impl)
    alias(libs.plugins.nowtest.android.library.compose)
    alias(libs.plugins.nowtest.android.library.jacoco)
}
android {
    namespace = "com.lhzkml.nowtest.feature.interests.impl"
}

dependencies {
    implementation(projects.feature.interests.api)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
