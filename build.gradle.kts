// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Apply false to the Android Application plugin at the root level
    alias(libs.plugins.android.application).apply(false)
    // Apply false to the Kotlin Android plugin at the root level
    alias(libs.plugins.kotlin.android).apply(false)
    // Apply false to the Kotlin Compose plugin at the root level
    alias(libs.plugins.kotlin.compose).apply(false)
}

buildscript {
    repositories {
        google()        // Ensure that Google's Maven repository is included
        mavenCentral()  // Maven Central repository for other dependencies
    }
    dependencies {
        // Classpath for the Android Gradle Plugin (AGP) - do NOT apply here
        classpath("com.android.tools.build:gradle:8.4.0") // Use the latest stable version
        // Classpath for the Kotlin Gradle Plugin - usually included with AGP
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22") // Use the latest stable version
        // Removed the Google Services classpath dependency
        // classpath("com.google.gms:google-services:4.3.15")
    }
}

// You can define additional configuration or dependencies for the entire project here