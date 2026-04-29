plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.sync.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.data)
    implementation(projects.sync.work)
}
