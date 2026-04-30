plugins {
    alias(libs.plugins.nowtest.android.feature.api)
}

android {
    namespace = "com.lhzkml.nowtest.feature.foryou.api"
}

dependencies {
    api(projects.core.navigation)
}
