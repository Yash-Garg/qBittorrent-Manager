@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dev.yashgarg.qbit.ui.compose"
    compileSdk = 33

    defaultConfig { minSdk = 24 }

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
    implementation(libs.androidx.glance)
    implementation(libs.compose.material.icons)
    implementation(projects.common)
    implementation(projects.bonsaiCore)
}
