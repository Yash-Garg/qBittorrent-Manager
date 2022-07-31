@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        includeBuild("build-logic")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("VERSION_CATALOGS")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "qbittorrent-kt"

include(":app")
