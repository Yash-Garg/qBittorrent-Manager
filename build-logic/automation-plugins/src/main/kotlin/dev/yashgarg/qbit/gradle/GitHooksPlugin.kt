package dev.yashgarg.qbit.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register

@Suppress("Unused")
class GitHooksPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register<Copy>("copyGitHooks") {
            description = "Copies the git hooks from /hooks to the .git/hooks folder."
            from("${project.rootDir}/hooks/") {
                include("**/*.sh")
                rename("(.*).sh", "$1")
            }
            into("${project.rootDir}/.git/hooks")
        }
    }
}
