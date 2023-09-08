package dev.yashgarg.qbit.gradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("Unused")
class KotlinCommonPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.run {
            withType<JavaCompile>().configureEach {
                sourceCompatibility = JavaVersion.VERSION_17.toString()
                targetCompatibility = JavaVersion.VERSION_17.toString()
            }
            withType<KotlinCompile>().configureEach {
                kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }
            }
        }
    }
}
