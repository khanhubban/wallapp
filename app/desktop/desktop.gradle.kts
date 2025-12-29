/**
 * Structure from https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

group = "com.wallapp.mawa"
version = "1.0.0"

dependencies {
    implementation(compose.desktop.currentOs)
    api(compose.runtime)
    api(compose.foundation)
    api(compose.material)
    api(compose.ui)
    api(compose.materialIconsExtended)

    implementation(libs.koin.core)
    implementation(libs.ktor.client.core)

    implementation(project(":shared"))
    implementation(project(":shared:app:app-adapter"))
    implementation(project(":shared:app:app-initializer"))
    implementation(project(":shared:core:common"))
    implementation(project(":shared:core:math"))
    implementation(project(":shared:core:network"))
    implementation(project(":shared:core:pixel"))
    implementation(project(":shared:core:unit"))
    implementation(project(":shared:core:viewmodel"))
    implementation(project(":shared:data:content"))
    implementation(project(":shared:data:content-network"))
    implementation(project(":shared:data:mediamap"))
    implementation(project(":shared:data:mediamap-network"))
    implementation(project(":shared:data:remoteendpoint"))
    implementation(project(":shared:data:resources"))
    implementation(project(":shared:data:resources-oss"))
    implementation(project(":shared:di:di-base"))
    implementation(project(":shared:di:di-app"))
    implementation(project(":shared:domain:content-state"))
    implementation(project(":shared:system:common"))
}

compose.desktop {
    application {
        mainClass = "wallapp.DesktopKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "MyProject"
            macOS {
                bundleID = "com.example.wallapp.desktop"
            }

            println("Desktop build directory: ${project.buildDir}")
            outputBaseDir.set(project.buildDir)
        }
    }
}
