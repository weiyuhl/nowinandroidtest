plugins {
    alias(libs.plugins.nowinandroid.jvm.library)
    alias(libs.plugins.nowinandroid.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
