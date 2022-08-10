@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "cafe.adriel.bonsai.core"
    compileSdk = 32

    buildFeatures {
        compose = true
        composeOptions {
            useLiveLiterals = false
            kotlinCompilerExtensionVersion =
                libs.compose.compiler.get().versionConstraint.requiredVersion
        }
    }
}

dependencies {
    api(libs.bundles.compose)
    implementation(libs.compose.material.icons)
}
