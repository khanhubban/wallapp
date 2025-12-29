package wallapp.preferences

import wallapp.buildconfig.BuildConfig
import wallapp.prefs.PreferenceDefaultsProvider
import wallapp.prefs.PreferenceDefaultsProviderConfig
import wallapp.system.platform.PlatformFeature
import wallapp.theme.ThemeType


class PreferenceDefaultsProviderDefault(
    private val config: PreferenceDefaultsProviderConfig,
    private val buildConfig: BuildConfig,
) : PreferenceDefaultsProvider(config) {

    override val themeType: String
        get() = ThemeType.System.name
    override val appInstallTime: Long
        get() = config.currentTime
    override val appInstallVersionName: String
        get() = buildConfig.appVersion.versionName
    override val dimFeedPreviews: Boolean
        get() = false
    override val exposeFavorites: Boolean
        get() = true
    override val enableZoomPan: Boolean
        get() = true
    override val showToastOnWallpaperChange: Boolean
        get() = false
    override val showTutorialOnFirstRun: Boolean
        get() = false
    override val tutorialFeedDismissed: Boolean
        get() = true
    override val strictTutorial: Boolean
        get() = false
    override val reviewFeedItemDismissedCount: Int
        get() = 0
    override val acceptedTerms: Boolean
        get() = false
    override val debugLicenseState: Int
        get() = config.debugLicenseState
    override val useDebugBillingManager: Boolean
        get() {
            // iOS uses the same BundleId for debug and release builds, and both use proper billing.
            return if (PlatformFeature.IsIos) {
                false
            } else if (PlatformFeature.IsAndroid) {
                buildConfig.debug
            } else if (PlatformFeature.IsDesktop) {
                true
            } else {
                throw IllegalStateException("Unsupported platform")
            }
        }
    override val enableLogging: Boolean
        get() = buildConfig.debug
}
