
import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
    id("wallapp.compose")
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
                implementation(project(":shared:core:common"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:unit"))
                implementation(project(":shared:data:resources"))
                implementation(project(":shared:presentation:image-ui"))
                implementation(project(":shared:presentation:pixel-ui"))

                implementation(compose.animationGraphics)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.runtime)
                implementation(compose.ui)

                implementation(libs.seiko.imageloader)
            }
        }

        val androidDebug by creating {
            dependencies {
                implementation(compose.uiTooling)
                implementation(libs.compose.ui.tooling)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.gms.play.services.auth)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

            }
        }
    }
}

android {
    namespace = "wallapp.account.ui"
}