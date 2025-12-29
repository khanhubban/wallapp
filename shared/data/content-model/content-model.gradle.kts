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
                implementation(libs.skie.annotations)

                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:unit"))
                implementation(project(":shared:data:base"))
                implementation(project(":shared:data:billing:api"))
            }
        }
    }
}

android {
    namespace = "wallapp.data.content.model"
}