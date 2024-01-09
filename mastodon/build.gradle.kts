plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "fediapi.mastodon"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

kotlin {
    androidTarget()
    jvm()
    js {
        browser()
        nodejs()
    }

    jvmToolchain(17)
    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":fediapi-core"))
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}