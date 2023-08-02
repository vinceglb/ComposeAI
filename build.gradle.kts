
plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("android") version libs.versions.kotlin apply false
    kotlin("multiplatform") version libs.versions.kotlin apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    // TODO add SQLDelight
}

buildscript {
    dependencies {
        classpath(libs.buildkonfig.gradle.plugin)
        classpath(libs.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
    }
}
