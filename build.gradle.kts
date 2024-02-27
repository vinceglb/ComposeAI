plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.googleServices) apply false
    // alias(libs.plugins.sqlDelight) apply false
    alias(libs.plugins.buildKonfig) apply false
    alias(libs.plugins.libres) apply false
}

buildscript {
    dependencies {
        classpath(libs.buildkonfig.gradle.plugin)
        classpath(libs.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
    }
}
