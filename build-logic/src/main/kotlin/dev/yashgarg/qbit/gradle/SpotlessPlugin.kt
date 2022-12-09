package dev.yashgarg.qbit.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

@Suppress("Unused")
class SpotlessPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(SpotlessPlugin::class)
        project.extensions.getByType<SpotlessExtension>().run {
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

            format("xml") {
                target("**/*.xml")
                targetExclude("**/build/", ".idea/")
                trimTrailingWhitespace()
                indentWithSpaces()
                endWithNewline()
            }
        }
    }
}
