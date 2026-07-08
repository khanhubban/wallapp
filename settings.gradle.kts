pluginManagement {
    includeBuild("tooling")
    repositories {
        google {
            mavenContent {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "WallApp"

// For reasons unknown, attempting to run via Xcode causes ":android:app" to be built. The xcodeproj
// file passes "-PexcludeAndroidModule" as part of its Run Script, which works around this.
if (!extra.has("excludeAndroidModule")) {
    include(":app:android")
    include(":app:desktop")
}

include(
    ":service:service-common",
    ":service:content-pipeline",
    ":service:service-export-remoteapi",
    ":shared",
    ":shared:admin:admin",
    ":shared:app:app-adapter",
    ":shared:app:app-adapter-debug",
    ":shared:app:app-di",
    ":shared:app:app-initializer",
    ":shared:core:common",
    ":shared:core:di",
    ":shared:core:download",
    ":shared:core:initializer",
    ":shared:core:math",
    ":shared:core:monitoring:monitoring-api",
    ":shared:core:monitoring:monitoring-impl",
    ":shared:core:network",
    ":shared:core:pixel",
    ":shared:core:resource",
    ":shared:core:security",
    ":shared:core:setting",
    ":shared:core:unit",
    ":shared:core:viewmodel",
    ":shared:data:account",
    ":shared:data:account-api",
    ":shared:data:account-data",
    ":shared:data:account-data-firebase",
    ":shared:data:account-firebase",
    ":shared:data:account-model",
    ":shared:data:account-signin",
    ":shared:data:ads-admob",
    ":shared:data:ads-api",
    ":shared:data:ads-internal",
    ":shared:data:base",
    ":shared:data:billing:api",
    ":shared:data:billing:debug",
//    ":shared:data:billing:google-play",
//    ":shared:data:billing:google-play-debug",
    ":shared:data:billing:revenuecat",
    ":shared:data:cloudmessaging-api",
    ":shared:data:cloudmessaging-firebase",
    ":shared:data:content",
    ":shared:data:content-api",
    ":shared:data:content-model",
    ":shared:data:content-network",
    ":shared:data:datafactory",
    ":shared:data:deeplink",
    ":shared:data:firebase",
    ":shared:data:licensing-api",
    ":shared:data:licensing-debug",
    ":shared:data:mediamap",
    ":shared:data:mediamap-network",
    ":shared:data:privacymessaging-api",
    ":shared:data:privacymessaging-google",
    ":shared:data:profileimage-api",
    ":shared:data:profileimage-impl",
    ":shared:data:purchase",
    ":shared:data:remoteapi",
    ":shared:data:remoteconfig-api",
    ":shared:data:remoteconfig-firebase",
    ":shared:data:remoteendpoint",
    ":shared:data:remotepaywall",
    ":shared:data:resources",
    ":shared:data:search",
    ":shared:data:search-api",
    ":shared:data:search-model",
    ":shared:data:search-network",
    ":shared:data:server-firebase",
    ":shared:data:toolkit",
    ":shared:data:wallpaper",
    ":shared:di:di-app",
    ":shared:di:di-base",
    ":shared:di:di-buildconfig-debug",
    ":shared:di:di-buildconfig-release",
    ":shared:domain:ads-support",
    ":shared:domain:billing-support",
    ":shared:domain:content-state",
    ":shared:domain:license-state",
    ":shared:domain:licensing-billing",
    ":shared:domain:remotepaywall-support",
    ":shared:interop:bridge",
    ":shared:interop:initialization",
    ":shared:presentation:account-ui",
    ":shared:presentation:compose-toolbox",
    ":shared:presentation:image-ui",
    ":shared:presentation:pixel-ui",
    ":shared:presentation:remotepaywall-ui",
    ":shared:presentation:theme-ui",
    ":shared:presentation:wallapp-ui",
    ":shared:system:common",
    ":shared:test:test-common",
)

fun renameBuildFileToModuleName(project: ProjectDescriptor) {
    project.buildFileName = "${project.name}.gradle.kts"
    project.children.forEach { child -> renameBuildFileToModuleName(child) }
}

// Renames every module's build.gradle file to use its name instead of `build`.
// E.g. `app/build.gradle` will become `app/app.gradle`
// The root build.gradle file will remain untouched
rootProject.children.forEach { subproject: ProjectDescriptor ->
    renameBuildFileToModuleName(subproject)
}

// Only include the baselineprofile module if the excludeAndroidModule property is not set
if (!extra.has("excludeAndroidModule")) {
    include(":baselineprofile")
}
