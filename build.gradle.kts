// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")        // Navigation - Safe Args
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}