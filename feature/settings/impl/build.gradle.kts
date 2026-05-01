plugins {
    alias(libs.plugins.nowtest.android.feature.impl)
    alias(libs.plugins.nowtest.android.library.compose)
    alias(libs.plugins.nowtest.android.library.jacoco)
}

android {
    namespace = "com.lhzkml.nowtest.feature.settings.impl"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.google.oss.licenses)
    implementation(projects.core.data)
    implementation(projects.feature.settings.api)

    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
