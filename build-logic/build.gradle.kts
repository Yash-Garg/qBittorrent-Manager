import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { `kotlin-dsl` }

dependencies {
    implementation(libs.build.spotless)
    implementation(libs.build.kotlin)
}

afterEvaluate {
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }
    }
}

gradlePlugin {
    plugins {
        register("spotless") {
            id = "dev.yashgarg.qbit.spotless"
            implementationClass = "dev.yashgarg.qbit.gradle.SpotlessPlugin"
        }
        register("githooks") {
            id = "dev.yashgarg.qbit.githooks"
            implementationClass = "dev.yashgarg.qbit.gradle.GitHooksPlugin"
        }
        register("kotlin-common") {
            id = "dev.yashgarg.qbit.kotlin-common"
            implementationClass = "dev.yashgarg.qbit.gradle.KotlinCommonPlugin"
        }
        register("kotlin-android") {
            id = "dev.yashgarg.qbit.kotlin-android"
            implementationClass = "dev.yashgarg.qbit.gradle.KotlinAndroidPlugin"
        }
    }
}
