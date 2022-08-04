@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "dev.yashgarg.qbit.ui.compose"
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

dependencies { api(libs.bundles.compose) }
