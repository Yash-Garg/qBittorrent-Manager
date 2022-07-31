// Top-level build file where you can add configuration options common to all sub-projects/modules.
@file:Suppress("DSL_SCOPE_VIOLATION")

import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.navigation.safeargs) apply false

    id("dev.yashgarg.qbit.spotless")
    id("dev.yashgarg.qbit.githooks")
}

val clean by tasks.existing(Delete::class) { delete(rootProject.buildDir) }

afterEvaluate { clean.dependsOn("copyGitHooks") }
