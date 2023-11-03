// Top-level build file where you can add configuration options common to all sub-projects/modules.
@file:Suppress("DSL_SCOPE_VIOLATION")

import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.binaryCompat) apply false
    alias(libs.plugins.sentry) apply false

    alias(libs.plugins.custom.githooks)
    alias(libs.plugins.custom.spotless)
    alias(libs.plugins.kotlin.common)
}

val clean by tasks.existing(Delete::class) { delete(rootProject.layout.buildDirectory) }

afterEvaluate {
    tasks.prepareKotlinBuildScriptModel.dependsOn(tasks.copyGitHooks, tasks.installGitHooks)
}
