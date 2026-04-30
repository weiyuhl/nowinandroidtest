plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.hilt)
}

android {
    namespace = "com.lhzkml.nowtest.core.data.test"
}

dependencies {
    api(projects.core.data)

    implementation(libs.hilt.android.testing)
}
