plugins {
    alias(libs.plugins.nowinandroid.android.feature.api)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.feature.search.api"
}

dependencies {
    implementation(projects.core.domain)
}
