plugins {
    alias(libs.plugins.nowtest.android.feature.impl)
    alias(libs.plugins.nowtest.android.library.compose)
}

android {
    namespace = "com.lhzkml.nowtest.feature.bookmarks.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.bookmarks.api)
    implementation(projects.feature.topic.api)

    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    androidTestImplementation(projects.core.testing)
}
