plugins {
    alias(libs.plugins.nowtest.android.feature.impl)
    alias(libs.plugins.nowtest.android.library.compose)
}

android {
    namespace = "com.lhzkml.nowtest.feature.bookmarks.impl"
}

dependencies {
    implementation(projects.feature.bookmarks.api)
}
