@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

plugins {
    id("dev.yashgarg.qbit.kotlin-android")
    alias(libs.plugins.android.library)
}

android {
    namespace = "dev.yashgarg.qbit.ui.compose"
    compileSdk = 33

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

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
