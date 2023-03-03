package dev.yashgarg.qbit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

@Suppress("Unused")
class KotlinAndroidPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.run {
            apply(KotlinAndroidPluginWrapper::class)
            apply(KotlinCommonPlugin::class)
        }
    }
}
