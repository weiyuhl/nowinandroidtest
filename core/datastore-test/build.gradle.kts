plugins {
    alias(libs.plugins.nowtest.android.library)
    alias(libs.plugins.nowtest.hilt)
}

android {
    namespace = "com.lhzkml.nowtest.core.datastore.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
}
