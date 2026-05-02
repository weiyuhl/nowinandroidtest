plugins {
    alias(libs.plugins.nowtest.android.route.scene)
    alias(libs.plugins.nowtest.android.library.compose)
    alias(libs.plugins.nowtest.android.library.jacoco)
}
android {
    namespace = "com.lhzkml.nowtest.route.test3.scene"
}

dependencies {
    implementation(projects.route.test3.contract)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
