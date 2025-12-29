import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:unit"))
                implementation(project(":shared:data:account"))
                implementation(project(":shared:data:account-signin"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:content-model"))
                implementation(project(":shared:data:mediamap"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:data:wallpaper"))
            }
        }
    }
}

android {
    namespace = "wallapp.data.factory"
}