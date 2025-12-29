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
                implementation(project(":shared:core:di"))
                implementation(project(":shared:core:pixel"))
                implementation(project(":shared:core:resource"))
                implementation(project(":shared:core:viewmodel"))
                implementation(project(":shared:data:ads-api"))
                implementation(project(":shared:data:content"))
                implementation(project(":shared:data:toolkit"))
                implementation(project(":shared:domain:ads-support"))
                implementation(project(":shared:domain:content-state"))
                implementation(project(":shared:presentation:account-ui"))
                implementation(project(":shared:presentation:remotepaywall-ui"))
                implementation(project(":shared:presentation:pixel-ui"))

                implementation(compose.animationGraphics)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation(compose.ui)

                implementation(libs.haze)
                implementation(libs.precompose.core)
                implementation(libs.valentinilk.shimmer)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.accompanist.systemuicontroller)
                implementation(libs.airbnb.android.lottie)
                implementation(libs.airbnb.android.lottie.compose)
                implementation(libs.coil)
                implementation(libs.coil.compose)

                implementation((libs.androidx.navigation.compose))
                implementation((libs.androidx.navigation.runtime))
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.compose.runtime.livedata)
                implementation(compose.preview)
            }
        }
        val desktopMain by getting

        val androidDebug by creating {
            dependencies {
                implementation(compose.uiTooling)
                implementation(libs.compose.ui.tooling)
            }
        }
    }
}

android {
    namespace = "wallapp.wallapp.ui"
}
