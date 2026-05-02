plugins {
    alias(libs.plugins.nowtest.android.route.contract)
}

android {
    namespace = "com.lhzkml.nowtest.route.test1.contract"
}

dependencies {
    api(projects.core.navigation)
}
