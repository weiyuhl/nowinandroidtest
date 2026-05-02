plugins {
    alias(libs.plugins.nowtest.android.route.scene)
    alias(libs.plugins.nowtest.android.library.compose)
}

android {
    namespace = "com.lhzkml.nowtest.route.test2.scene"
}

dependencies {
    implementation(projects.route.test2.contract)
}
