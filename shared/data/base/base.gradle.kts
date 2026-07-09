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
                implementation(project(":shared:core:unit"))
                api(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                api(project(":shared:core:setting"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "wallapp.base"
}