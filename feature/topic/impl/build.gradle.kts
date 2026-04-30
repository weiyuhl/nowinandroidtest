plugins {
    alias(libs.plugins.nowtest.android.feature.impl)
    alias(libs.plugins.nowtest.android.library.compose)
    alias(libs.plugins.nowtest.android.library.jacoco)
}

android {
    namespace = "com.lhzkml.nowtest.feature.topic.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.topic.api)

    implementation(libs.androidx.compose.material3.adaptive.navigation3)

    testImplementation(projects.core.testing)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
