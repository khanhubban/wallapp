
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
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:domain:content-state"))
                api(project(":shared:presentation:theme-ui"))

                implementation(compose.animationGraphics)
                implementation(compose.components.resources)
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
                implementation(libs.airbnb.android.lottie.compose)
                implementation(libs.androidx.media3.common)
                implementation(libs.androidx.media3.exoplayer)
                implementation(libs.androidx.media3.ui)
                implementation(libs.coil)
                implementation(libs.coil.compose)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.seiko.imageloader.extension.imageio)
            }
        }
    }
}

android {
    namespace = "wallapp.image.ui"
}