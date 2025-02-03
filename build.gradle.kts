plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin) // Update to the latest version
        classpath(libs.hilt.android.gradle.plugin)
    }
}