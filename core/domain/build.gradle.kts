plugins {
    alias(libs.plugins.nowinandroid.android.library)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)

    testImplementation(projects.core.testing)
}
