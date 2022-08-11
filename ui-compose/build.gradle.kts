@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "dev.yashgarg.qbit.ui.compose"
    compileSdk = 33

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
    implementation(projects.common)
    implementation(projects.bonsaiCore)
}
