package wallapp.resources.string

import wallapp.system.platform.PlatformFeature

/**
 * This is not so nice, but we can live with it for now to simplify string creation in tests.
 */
enum class StringArbitratorPlatform {
    Android,
    Ios,
    Desktop,
    ;

    companion object {
        fun arbitrated(): StringArbitratorPlatform {
            return when {
                PlatformFeature.IsAndroid -> Android
                PlatformFeature.IsIos -> Ios
                PlatformFeature.IsDesktop -> Desktop
                else -> throw IllegalStateException("Unknown platform")
            }
        }
    }
}