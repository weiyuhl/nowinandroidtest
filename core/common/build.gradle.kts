plugins {
    alias(libs.plugins.nowtest.jvm.library)
    alias(libs.plugins.nowtest.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
