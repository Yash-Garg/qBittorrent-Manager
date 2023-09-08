@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

plugins {
    id("dev.yashgarg.qbit.kotlin-android")
    alias(libs.plugins.android.library)
}

android {
    namespace = "dev.yashgarg.qbit.ui.compose"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }

    buildFeatures {
        compose = true
        composeOptions {
            useLiveLiterals = false
            kotlinCompilerExtensionVersion =
                libs.compose.compiler.get().versionConstraint.requiredVersion
        }
    }

    lint { baseline = file("lint-baseline.xml") }
}

dependencies {
    api(libs.bundles.compose)
    implementation(libs.compose.material.icons)
    implementation(projects.common)
    implementation(projects.bonsaiCore)
}
