import com.diffplug.gradle.spotless.SpotlessExtension

plugins { id("com.diffplug.spotless") }

configure<SpotlessExtension> {
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
