import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
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
                implementation(project(":shared:core:setting"))
                api(project(":shared:data:licensing-api"))
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockito.core)
                implementation(libs.robolectric)
                implementation(libs.jraska.livedata.testing)
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    namespace = "wallapp.license.state"
}

