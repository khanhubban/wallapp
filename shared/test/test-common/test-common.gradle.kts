import wallapp.tooling.extensions.configureAllSourceSets
import wallapp.tooling.extensions.desktop
import wallapp.tooling.extensions.iOS

plugins {
    id("wallapp.kotlin.multiplatform")
}

kotlin {
    androidTarget()
    desktop()
    iOS()

    sourceSets {
        configureAllSourceSets()

        commonMain {
            dependencies {
                api(libs.koin.core)

                implementation(project(":shared:app:app-initializer"))
                implementation(project(":shared:core:initializer"))
                implementation(project(":shared:di:di-app"))
                implementation(project(":shared:domain:content-state"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)

                /**
                 * Note: linking with test libraries here rather than in androidUnitTest so
                 * common code can access test code (such as creating a Context).
                 */
                implementation(libs.androidx.arch.core.testing)
                implementation(libs.androidx.test.core)
            }
        }

//        androidUnitTest {
//            dependencies {
//                implementation(libs.androidx.arch.core.testing)
//                implementation(libs.androidx.test.core)
//            }
//        }
    }
}

android {
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "wallapp.test.common"
}
