plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "fediapi"
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
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.client.core)
                api(libs.kotlinx.datetime)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
    }
}