import android.annotation.SuppressLint

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.diffplug.spotless")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "dev.yashgarg.qbit"
    compileSdk = 32

    defaultConfig {
        applicationId = "dev.yashgarg.qbit"
        minSdk = 24
        @SuppressLint("OldTargetApi")
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions { jvmTarget = "1.8" }

    buildFeatures { viewBinding = true }

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

spotless {
    kotlin {
        ktfmt().kotlinlangStyle()
        target("**/*.kt")
        targetExclude("**/build/")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        ktfmt().kotlinlangStyle()
        target("**/*.gradle.kts")
        targetExclude("**/build/")
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(libs.google.material)
    implementation(libs.google.dagger.hilt)
    kapt(libs.google.dagger.hilt.compiler)

    debugImplementation(libs.square.leakcanary)
}
