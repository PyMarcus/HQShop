// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    //firebase
    id("com.google.gms.google-services") version "4.4.1" apply false
    // hild
    id("com.google.dagger.hilt.android") version "2.44" apply false

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
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}


