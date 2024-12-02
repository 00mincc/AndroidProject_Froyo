plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0") // Android Gradle 플러그인
        classpath ("com.google.gms:google-services:4.3.15")
    }
}
