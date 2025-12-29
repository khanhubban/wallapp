package wallapp.data.osslicense

import wallapp.system.platform.PlatformFeature

object OssLicenses {

    private val Common = listOf(
        OssLicense("Montserrat", "Open Font License", licenseUrl = "https://raw.githubusercontent.com/JulietaUla/Montserrat/master/OFL.txt"),
        OssLicense("PreCompose", "Apache 2.0", licenseUrl = "https://github.com/Tlaster/PreCompose/blob/master/LICENSE"),
        OssLicense("SKIE", "Apache 2.0", licenseUrl = "https://github.com/touchlab/SKIE/blob/main/LICENSE"),
        OssLicense("androidx", "Apache 2.0", licenseUrl = "https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt"),
        OssLicense("coil", "Apache 2.0", licenseUrl = "https://github.com/coil-kt/coil/blob/main/LICENSE.txt"),
        OssLicense("compose-imageloader", "MIT License", licenseUrl = "https://github.com/qdsfdhvh/compose-imageloader/blob/master/LICENSE"),
        OssLicense("compose-multiplatform", "Apache 2.0", licenseUrl = "https://github.com/JetBrains/compose-multiplatform/blob/master/LICENSE.txt"),
        OssLicense("compose-shimmer", "Apache 2.0", "https://github.com/valentinilk/compose-shimmer"),
        OssLicense("cryptography-kotlin", "Apache 2.0", licenseUrl = "https://raw.githubusercontent.com/whyoleg/cryptography-kotlin/refs/heads/main/LICENSE"),
        OssLicense("firebase-kotlin-sdk", "Apache 2.0", licenseUrl = "https://github.com/GitLiveApp/firebase-kotlin-sdk/blob/master/LICENSE"),
        OssLicense("haze", "Apache 2.0", licenseUrl = "https://github.com/chrisbanes/haze/blob/main/LICENSE"),
        OssLicense("koin", "Apache 2.0", licenseUrl = "https://github.com/InsertKoinIO/koin/blob/main/LICENSE"),
        OssLicense("kotlin", "Apache 2.0", licenseUrl = "https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt"),
        OssLicense("kotlinx-atomicfu", "Apache 2.0", licenseUrl = "https://github.com/Kotlin/kotlinx-atomicfu/blob/master/LICENSE.txt"),
        OssLicense("kotlinx-datetime", "Apache 2.0", licenseUrl = "https://github.com/Kotlin/kotlinx-datetime/blob/master/LICENSE.txt"),
        OssLicense("kotlinx.coroutines", "Apache 2.0", licenseUrl = "https://github.com/Kotlin/kotlinx.coroutines/blob/master/LICENSE.txt"),
        OssLicense("kotlinx.serialization", "Apache 2.0", licenseUrl = "https://github.com/Kotlin/kotlinx.serialization/blob/master/LICENSE.txt"),
        OssLicense("ksp", "Apache 2.0", licenseUrl = "https://github.com/google/ksp/blob/main/LICENSE"),
        OssLicense("ktor", "Apache 2.0", licenseUrl = "https://github.com/ktorio/ktor/blob/main/LICENSE"),
        OssLicense("multiplatform-settings", "Apache 2.0", licenseUrl = "https://github.com/russhwolf/multiplatform-settings/blob/main/LICENSE.txt"),
        OssLicense("okio", "Apache 2.0", licenseUrl = "https://github.com/square/okio/blob/master/LICENSE.txt"),
        OssLicense("OpenSSL", "Apache 2.0", licenseUrl = "https://raw.githubusercontent.com/openssl/openssl/refs/heads/master/LICENSE.txt"),
        OssLicense("tivi", "Apache 2.0", licenseUrl = "https://github.com/chrisbanes/tivi/blob/main/LICENSE"),
    )

    private val Android = listOf(
        OssLicense("ProcessPhoenix", "Apache 2.0", licenseUrl = "https://github.com/JakeWharton/ProcessPhoenix/blob/trunk/LICENSE.txt"),
        OssLicense("accompanist", "Apache 2.0", licenseUrl = "https://github.com/google/accompanist/blob/main/LICENSE"),
        OssLicense("firebase-android-sdk", "Apache 2.0", licenseUrl = "https://github.com/firebase/firebase-android-sdk/blob/main/LICENSE"),
        OssLicense("kpermissions", "Apache 2.0", licenseUrl = "https://github.com/fondesa/kpermissions/blob/master/LICENSE"),
        OssLicense("lottie-android", "Apache 2.0", licenseUrl = "https://github.com/airbnb/lottie-android/blob/master/LICENSE"),
        OssLicense("material-components-android", "Apache 2.0", licenseUrl = "https://github.com/material-components/material-components-android/blob/master/LICENSE"),
        OssLicense("okhttp", "Apache 2.0", licenseUrl = "https://github.com/square/okhttp/blob/master/LICENSE.txt"),
        OssLicense("purchases-android", "MIT License", licenseUrl = "https://github.com/RevenueCat/purchases-android/blob/main/LICENSE"),
        OssLicense("timber", "Apache 2.0", licenseUrl = "https://github.com/JakeWharton/timber/blob/trunk/LICENSE.txt"),
        OssLicense("zip4j", "Apache 2.0", licenseUrl = "https://github.com/srikanth-lingala/zip4j/blob/master/LICENSE"),
    )

    private val Ios = listOf(
        OssLicense("AppAuth", "Apache 2.0", licenseUrl = "https://github.com/openid/AppAuth-iOS/blob/master/LICENSE"),
        OssLicense("BoringSSL-GRPC", "Mixed", licenseUrl = "https://github.com/grpc/grpc/blob/master/LICENSE"),
        OssLicense("Cache", "MIT", licenseUrl = "https://github.com/hyperoslo/Cache/blob/master/LICENSE.md"),
        OssLicense("CrashKiOS", "Apache 2.0", licenseUrl = "https://github.com/touchlab/CrashKiOS/blob/main/LICENSE.txt"),
        OssLicense("GTMAppAuth", "Apache 2.0", licenseUrl = "https://github.com/google/GTMAppAuth/blob/master/LICENSE"),
        OssLicense("GTMSessionFetcher", "Apache 2.0", licenseUrl = "https://github.com/google/gtm-session-fetcher/blob/main/LICENSE"),
        OssLicense("GoogleDataTransport", "Apache 2.0", licenseUrl = "https://github.com/google/GoogleDataTransport/blob/main/LICENSE"),
        OssLicense("GoogleSignIn", "Apache 2.0", licenseUrl = "https://github.com/google/GoogleSignIn-iOS/blob/main/LICENSE"),
        OssLicense("GoogleUtilities", "Apache 2.0", licenseUrl = "https://github.com/google/GoogleUtilities/blob/main/LICENSE"),
        OssLicense("Kingfisher", "MIT", licenseUrl = "https://github.com/onevcat/Kingfisher/blob/master/LICENSE"),
        OssLicense("RecaptchaInterop", "Apache 2.0", licenseUrl = "https://github.com/google/interop-ios-for-google-sdks/blob/main/LICENSE"),
        OssLicense("ScalingCarousel", "MIT", licenseUrl = "https://github.com/aataraxiaa/ScalingCarousel"),
        OssLicense("abseil", "Apache 2.0", licenseUrl = "https://github.com/abseil/abseil-cpp/blob/master/LICENSE"),
        OssLicense("firebase-ios-sdk", "Apache 2.0", licenseUrl = "https://github.com/firebase/firebase-ios-sdk/blob/main/LICENSE"),
        OssLicense("grpc-ios", "Apache 2.0", licenseUrl = "https://github.com/grpc/grpc-ios/blob/main/LICENSE"),
        OssLicense("leveldb-library", "New BSD", licenseUrl = "https://github.com/google/leveldb/blob/main/LICENSE"),
        OssLicense("lottie-ios", "Apache 2.0", licenseUrl = "https://github.com/airbnb/lottie-ios/blob/master/LICENSE"),
        OssLicense("nanopb", "zlib", licenseUrl = "https://github.com/nanopb/nanopb/blob/master/LICENSE.txt"),
        OssLicense("promises", "Apache 2.0", licenseUrl = "https://github.com/google/promises/blob/master/LICENSE"),
        OssLicense("purchases-ios", "MIT", licenseUrl = "https://github.com/RevenueCat/purchases-ios/blob/main/LICENSE"),
    )


    val CurrentPlatform: List<OssLicense> by lazy {
        (if (PlatformFeature.IsIos) {
            Common + Ios
        } else if (PlatformFeature.IsAndroid) {
            Common + Android
        } else {
            Common
        })
            .sortedBy { it.name.lowercase() }
    }
}
