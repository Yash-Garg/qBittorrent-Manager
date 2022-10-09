@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dev.yashgarg.qbit.common"
    compileSdk = 33
}

dependencies {
    api(libs.qbittorrent.models)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity)
}
