import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    id("wallapp.compose")
    alias(libs.plugins.serialization)
}
apply(from = "${rootProject.projectDir}/moduleflavors.gradle")

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                implementation(libs.kotlin.reflect)
                implementation(libs.kotlinx.serialization.json)

                api(project(":shared:core:resource"))
                api(project(":shared:data:resources"))
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:unit"))
            }
        }

        iosMain {
            dependencies {
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
    }
}

android {
    buildFeatures {
        resValues = true
    }
    namespace = "wallapp.resources"

    sourceSets["main"].apply {
        res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "wallapp.resources"
    generateResClass = auto
}
