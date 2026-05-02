plugins {
    alias(libs.plugins.nowtest.android.route.scene)
    alias(libs.plugins.nowtest.android.library.compose)
}

android {
    namespace = "com.lhzkml.nowtest.route.test1.scene"
}

dependencies {
    implementation(projects.route.test1.contract)
}
