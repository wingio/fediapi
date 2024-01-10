import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.6.0-alpha01"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":mastodon"))

    implementation(compose.material3)
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material", module = "material")
    }
}

compose.desktop.application {
    mainClass = "TestingKt"

    nativeDistributions {
        packageName = "FediAPI Testing"
        description = "FediAPI Testing"
        packageVersion = "1.0.0"

        targetFormats(TargetFormat.Exe)
    }
}