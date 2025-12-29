plugins {
    kotlin("jvm")
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.compose.multiplatform)
}

group = "com.wallapp.service.common"
version = "1.0.0"

dependencies {
    api(libs.firebase.admin)
    api(libs.koin.core)
    api(libs.kotlinx.coroutines.core)
    api(libs.ktor.client.core)

    api(project(":shared"))
    api(project(":shared:core:common"))
    api(project(":shared:core:math"))
    api(project(":shared:core:network"))
    api(project(":shared:core:pixel"))
    api(project(":shared:core:unit"))
    api(project(":shared:data:content"))
    api(project(":shared:data:content-network"))
    api(project(":shared:data:mediamap"))
    api(project(":shared:data:mediamap-network"))
    api(project(":shared:data:server-firebase"))
    api(project(":shared:domain:content-state"))
    api(project(":shared:system:common"))
}
