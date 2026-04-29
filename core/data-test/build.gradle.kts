plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.data.test"
}

dependencies {
    api(projects.core.data)

    implementation(libs.hilt.android.testing)
}
