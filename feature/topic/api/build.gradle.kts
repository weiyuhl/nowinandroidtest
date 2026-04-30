plugins {
    alias(libs.plugins.nowtest.android.feature.api)
    alias(libs.plugins.nowtest.android.feature.impl)
    alias(libs.plugins.nowtest.android.library.compose)
}

android {
    namespace = "com.lhzkml.nowtest.feature.topic.api"
}
