plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "dev.yashgarg.qbit.common"
    compileSdk = 32
}

dependencies {
    api(libs.qbittorrent.models)
    implementation(libs.androidx.core.ktx)
}
