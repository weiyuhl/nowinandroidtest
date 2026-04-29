plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.hilt)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.datastore.test"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
}
