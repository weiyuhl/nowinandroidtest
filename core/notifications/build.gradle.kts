plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.hilt)
}

android {
    namespace = "com.lhzkml.nowtest.core.notifications"
}

dependencies {
    implementation(projects.core.common)

    compileOnly(platform(libs.androidx.compose.bom))
}
