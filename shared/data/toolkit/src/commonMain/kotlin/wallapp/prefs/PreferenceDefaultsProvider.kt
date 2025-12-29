package wallapp.prefs

import wallapp.appicon.AppIcon
import wallapp.theme.ThemeType
import wallapp.time.getCurrentTimeMillis


/**
 * Provides default preference values. Useful for values that need to be configured at runtime,
 * on a per-flavor basis, etc.
 */
abstract class PreferenceDefaultsProvider(
    private val config: PreferenceDefaultsProviderConfig,
) {

    open val themeType: String
        get() = ThemeType.System.name

    open val appIcon: String
        get() = AppIcon.Default.name

    abstract val appInstallTime: Long

    abstract val appInstallVersionName: String

    open val animateWallpaper: Boolean
        get() = true

    open val showToastOnWallpaperChange: Boolean
        get() = true

    open val firstRunOnboardingDismissed: Boolean
        get() = false

    open val homeOnboardingDismissed: Boolean
        get() = false

    open val dimFeedPreviews: Boolean
        get() = true

    open val showTutorialOnFirstRun: Boolean
        get() = true

    open val tutorialFeedDismissed: Boolean
        get() = false

    abstract val strictTutorial: Boolean

    open val reviewFeedItemDismissedCount: Int
        get() = 0

    open val enableZoomPan: Boolean
        get() = false

    open val acceptedTerms: Boolean
        get() = false

    open val joinNewsletter: Boolean
        get() = true

    open val reportUsageStats: Boolean
        get() = true

    open val receiveNotifications: Boolean
        get() = true

    open val isOverviewInfoClosed: Boolean
        get() = true

    open val upgradeSuccessDismissed: Boolean
        get() = false

    open val debugLicenseState: Int
        get() = 456 // LICENSE_STATE_ALLOWED

    open val useDebugBillingManager: Boolean
        get() = false

    open val exposeFavorites: Boolean
        get() = false

    open val useMilitaryTimeFormat: Boolean
        get() = !config.isEnglishLanguage

    open val dynamicWallpaperId: String
        get() = ""

    open val enableLogging: Boolean
        get() = false
}


open class PreferenceDefaultsProviderMock(
    override val appInstallTime: Long = getCurrentTimeMillis(),
    override val appInstallVersionName: String = "",
    override val strictTutorial: Boolean = false,
    override val acceptedTerms: Boolean = false,
) : PreferenceDefaultsProvider(config = PreferenceDefaultsProviderConfigPreset())
