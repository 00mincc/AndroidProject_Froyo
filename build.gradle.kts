plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0") // Android Gradle 플러그인
    }
}
