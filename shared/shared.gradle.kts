
import co.touchlab.skie.configuration.EnumInterop
import co.touchlab.skie.configuration.FlowInterop
import co.touchlab.skie.configuration.SealedInterop
import co.touchlab.skie.configuration.SuspendInterop
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import wallapp.tooling.extensions.desktop

/**
 * Links with all iOS-compatible :shared modules. Creates the `:shared:embedAndSignAppleFrameworkForXcode`
 * task which is used on iOS.
 */

plugins {
    id("wallapp.kotlin.multiplatform")
    id("wallapp.compose")
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget()
    desktop()
    iosArm64()
    iosSimulatorArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(libs.koin.core)
                api(libs.precompose.core)
                api(libs.skie.annotations)
                api(libs.androidx.lifecycle.viewmodel)

                api(project(":shared:app:app-adapter"))
                api(project(":shared:core:common"))
                api(project(":shared:core:math"))
                api(project(":shared:core:monitoring:monitoring-api"))
                api(project(":shared:core:monitoring:monitoring-impl"))
                api(project(":shared:core:pixel"))
                api(project(":shared:core:resource"))
                api(project(":shared:core:security"))
                api(project(":shared:core:setting"))
                api(project(":shared:core:unit"))
                api(project(":shared:core:viewmodel"))
                api(project(":shared:data:ads-api"))
                api(project(":shared:data:ads-internal"))
                api(project(":shared:data:billing:api"))
                api(project(":shared:data:content"))
                api(project(":shared:data:content-model"))
                api(project(":shared:data:content-network"))
                api(project(":shared:data:licensing-api"))
                api(project(":shared:data:mediamap"))
                api(project(":shared:data:mediamap-network"))
                api(project(":shared:data:privacymessaging-api"))
                api(project(":shared:data:resources"))
                api(project(":shared:data:toolkit"))
                api(project(":shared:data:wallpaper"))
                api(project(":shared:di:di-base"))
                api(project(":shared:domain:ads-support"))
                api(project(":shared:domain:billing-support"))
                api(project(":shared:domain:content-state"))
                api(project(":shared:domain:license-state"))
                api(project(":shared:domain:licensing-billing"))
                api(project(":shared:interop:bridge"))
                api(project(":shared:interop:initialization"))
                api(project(":shared:presentation:compose-toolbox"))
                api(project(":shared:presentation:image-ui"))
                api(project(":shared:presentation:pixel-ui"))
                api(project(":shared:presentation:wallapp-ui"))
                api(project(":shared:system:common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        iosMain {
            dependencies {
                api(project(":shared:di:di-app"))
            }
        }

        targets.withType<KotlinNativeTarget>().configureEach {
            binaries.framework {
                // Xcode's "Other Linker Flags" must be configured with this name
                baseName = "WallApp"
                // Must set to true for now. See https://slack-chats.kotlinlang.org/t/10376516/i-use-some-skia-api-like-image-makefromencoded-in-my-compose#e4df9589-36ef-42fe-a9c6-e966f9b1d012
                isStatic = true

                //linkerOpts.add("-lsqlite3")

                // Add modules to be called from Swift/ObjC here
                export(libs.androidx.lifecycle.viewmodel)
                export(libs.androidx.lifecycle.viewmodel.compose)
                export(project(":shared:core:common"))
                export(project(":shared:core:download"))
                export(project(":shared:core:monitoring:monitoring-api"))
                export(project(":shared:core:pixel"))
                export(project(":shared:core:resource"))
                export(project(":shared:core:unit"))
                export(project(":shared:core:viewmodel"))
                export(project(":shared:data:account"))
                export(project(":shared:data:account-api"))
                export(project(":shared:data:account-signin"))
                export(project(":shared:data:ads-api"))
                export(project(":shared:data:billing:api"))
                export(project(":shared:data:billing:revenuecat"))
                export(project(":shared:data:content-model"))
                export(project(":shared:data:deeplink"))
                export(project(":shared:data:privacymessaging-api"))
                export(project(":shared:data:remotepaywall"))
                export(project(":shared:data:resources"))
                export(project(":shared:data:toolkit"))
                export(project(":shared:data:wallpaper"))
                export(project(":shared:di:di-app"))
                export(project(":shared:domain:ads-support"))
                export(project(":shared:domain:content-state"))
                export(project(":shared:interop:bridge"))
                export(project(":shared:interop:initialization"))
                export(project(":shared:presentation:wallapp-ui"))
            }
        }
    }
}

android {
    namespace = "wallapp.shared.all"
}

skie {
    features {
        group {
            FlowInterop.Enabled(false)
            SuspendInterop.Enabled(false)
            SealedInterop.Enabled(false)
            EnumInterop.Enabled(false)
        }
    }
    analytics {
        disableUpload.set(true)
    }
}