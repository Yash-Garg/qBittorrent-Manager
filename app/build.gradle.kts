@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION", "KaptUsageInsteadOfKsp")

val commitHash: String by lazy {
    providers
        .exec { commandLine("git").args("rev-parse", "--short", "HEAD").workingDir(projectDir) }
        .standardOutput
        .asText
        .get()
        .trim()
}

plugins {
    alias(libs.plugins.android.application)
    id("dev.yashgarg.qbit.kotlin-android")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.sentry)
}

android {
    namespace = "dev.yashgarg.qbit"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.yashgarg.qbit"
        minSdk = 28
        targetSdk = 34
        versionCode = 17
        versionName = "v0.2.5-$commitHash"

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        setProperty("archivesBaseName", "${defaultConfig.applicationId}-$versionName")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }

    val isGithubCi = System.getenv("GITHUB_CI") != null
    if (isGithubCi) {
        signingConfigs {
            register("release") {
                storeFile = file("keystore/qbit-key.jks")
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            }
        }
        buildTypes.getByName("release") { signingConfig = signingConfigs.getByName("release") }
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            versionNameSuffix = "-release"
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("benchmark") {
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }

    flavorDimensions += listOf("app")
    productFlavors {
        create("nonFree") {
            dimension = "app"
            manifestPlaceholders["sentryDsn"] = System.getenv("SENTRY_DSN") ?: ""
        }

        create("free") {
            dimension = "app"
            isDefault = true
            manifestPlaceholders["sentryDsn"] = ""
        }
    }

    sentry {
        ignoredBuildTypes.set(setOf("benchmark", "debug"))
        ignoredFlavors.set(setOf("free"))
    }

    androidComponents {
        beforeVariants {
            if (it.name.contains("benchmark", true)) {
                it.enable = false
            }
        }
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
        warningsAsErrors = true
        disable.add("PluralsCandidate")
        baseline = file("lint-baseline.xml")
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion =
            libs.compose.compiler.get().versionConstraint.requiredVersion
    }

    packaging {
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
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.lifecycle.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.recyclerview.selection)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.work.ktx)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.compiler)

    implementation(libs.bundles.compose)

    implementation(libs.google.material)
    implementation(libs.google.dagger.hilt)
    kapt(libs.google.dagger.hilt.compiler)

    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.kotlinx.serialization)

    implementation(projects.uiCompose)
    implementation(projects.common)
    implementation(projects.clientWrapper.client)
    implementation(projects.clientWrapper.models)

    debugImplementation(libs.tools.leakcanary)
    implementation(libs.tools.kotlin.result)
    implementation(libs.tools.cascade)
    implementation(libs.tools.lottie)
    implementation(libs.tools.whatthestack)

    testImplementation(libs.bundles.testing)
}
