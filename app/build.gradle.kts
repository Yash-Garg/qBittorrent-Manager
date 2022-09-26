@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import java.io.ByteArrayOutputStream

val commitHash: String by lazy {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git").args("rev-parse", "--short", "HEAD").workingDir(projectDir)
        standardOutput = stdout
    }
    stdout.toString().trim()
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "dev.yashgarg.qbit"
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.yashgarg.qbit"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.1"

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "${defaultConfig.applicationId}-$commitHash")
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions { jvmTarget = "1.8" }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion =
            libs.compose.compiler.get().versionConstraint.requiredVersion
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "**/kotlin/**"
            excludes += "**/*.txt"
            excludes += "**/*.xml"
            excludes += "**/*.properties"
        }
    }
}

kapt {
    correctErrorTypes = true
    arguments { arg("room.schemaLocation", "$projectDir/schemas") }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(libs.bundles.compose)

    implementation(libs.google.material)
    implementation(libs.google.dagger.hilt)
    kapt(libs.google.dagger.hilt.compiler)

    implementation(libs.ktor.android)
    implementation(libs.ktor.logging)
    implementation(libs.qbittorrent.client)

    implementation(projects.uiCompose)
    implementation(projects.common)

    debugImplementation(libs.tools.leakcanary)
    implementation(libs.tools.kotlin.result)
    implementation(libs.tools.cascade)
    implementation(libs.tools.lottie)

    testImplementation(libs.bundles.testing)
    coreLibraryDesugaring(libs.tools.desugar)
}
