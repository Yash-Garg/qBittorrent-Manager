@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

plugins {
    id("dev.yashgarg.qbit.kotlin-android")
    alias(libs.plugins.android.test)
}

android {
    namespace = "dev.yashgarg.benchmark"
    compileSdk = 34

    defaultConfig {
        minSdk = 33
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        missingDimensionStrategy("app", "nonFree", "free")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It"s signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

    lint { baseline = file("lint-baseline.xml") }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
    implementation(libs.androidx.test.junit)
    implementation(libs.androidx.test.espresso)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.benchmark.junit)
}

androidComponents { beforeVariants(selector().all()) { it.enable = it.buildType == "benchmark" } }
