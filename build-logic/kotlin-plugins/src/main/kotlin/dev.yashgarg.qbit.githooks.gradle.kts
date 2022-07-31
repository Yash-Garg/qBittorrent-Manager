val copyGitHooks by
    tasks.registering(Copy::class) {
        description = "Copies the git hooks from /hooks to the .git/hooks folder."
        group = "git hooks"
        from("${rootDir}/hooks/") {
            include("**/*.sh")
            rename("(.*).sh", "$1")
        }
        into("${rootDir}/.git/hooks")
    }
