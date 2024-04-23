// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    //firebase
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.0"
}

// safe args settings
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io")}
    }
    dependencies {
        val nav_version = "2.7.7"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
    }
}


