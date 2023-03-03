@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.binaryCompat)
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.clientWrapper.models)
                implementation(libs.coroutines.core)
                implementation(libs.kotlinx.serialization)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization)
            }
        }

        val jvmMain by getting { dependencies { implementation(kotlin("stdlib-jdk8")) } }
    }
}
