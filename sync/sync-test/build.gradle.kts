plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.hilt)
}

android {
    namespace = "com.lhzkml.nowtest.core.sync.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.data)
    implementation(projects.sync.work)
}
