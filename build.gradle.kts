buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Esto fuerza a Gradle a usar la versión correcta durante la fase de sincronización
        classpath("com.squareup:javapoet:1.13.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}
