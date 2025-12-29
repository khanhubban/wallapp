@file:Suppress("MemberVisibilityCanBePrivate")

package wallapp.prefs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.apphistory.AppHistoryManagerCache
import wallapp.content.model.WallpaperScreenTheme
import wallapp.preference.MutableObservableValue
import wallapp.preference.PreferenceInfo
import wallapp.process.preference.ProcessBridgePreferenceManager
import wallapp.settings.SettingFactory.mutableSetting
import wallapp.settings.SettingFactory.mutableSettingFlow
import wallapp.settings.SettingFactory.staticSetting
import wallapp.settings.Settings
import wallapp.settings.SettingsUpdateDispatcher


open class DevicePreferenceStorageDefault(
    defaults: PreferenceDefaults,
    internal val settings: Settings,
    internal val processBridgePreferenceManager: ProcessBridgePreferenceManager,
    private val coroutineScopeMain: CoroutineScope,
) : DevicePreferenceStorage, AppHistoryManagerCache {

    internal val updateDispatcher = SettingsUpdateDispatcher(settings)

    override val appInstallTime: Long = staticSetting(
        defaults.appInstallTime,
        settings,
        coroutineScopeMain,
        initialValueResolver = { defaults.appInstallTime.default() },
    )

    override val appInstallVersionName = staticSetting(
        defaults.appInstallVersionName,
        settings,
        coroutineScopeMain,
        initialValueResolver = { defaults.appInstallVersionName.default() },
    )

    override val lastAppRunVersionName: MutableObservableValue<String> = mutablePreference(defaults.lastAppRunVersionName)

    override val exposeUpgradeScrollScreen = mutablePreference(defaults.exposeUpgradeScrollScreen)

    override val allWallpapersUnlocked = mutablePreference(defaults.allWallpapersUnlocked)
    override val isCurrentSystemWallpaperApp = mutablePreference(defaults.isCurrentSystemWallpaperApp)
    override val isCurrentSystemWallpaperAppRefreshTime = mutablePreference(defaults.isCurrentSystemWallpaperAppRefreshTime)
    override val lastHomeScreenWallpaperData = mutablePreferenceFlow(defaults.lastHomeScreenWallpaperData)
    override val lastLockScreenWallpaperData = mutablePreferenceFlow(defaults.lastLockScreenWallpaperData)
    override val lastNetworkRefreshTime = mutablePreference(defaults.lastNetworkRefreshTime)
    override val lastNetworkApiPath = mutablePreference(defaults.lastNetworkApiPath)
    override val lastKnownDarkModeState = mutablePreference(defaults.lastKnownDarkModeState)
    override val bucketOverride = mutablePreference(defaults.bucketOverride)
    override val appShowing = mutablePreference(defaults.appShowing)
    override val firstRunOnboardingDismissed = mutablePreferenceFlow(defaults.firstRunOnboardingDismissed)
    override val homeOnboardingDismissed = mutablePreferenceFlow(defaults.homeOnboardingDismissed)
    override val deepLinkRemixes = mutablePreference(defaults.deepLinkRemixes)
    override val wallpaperHistory = mutablePreference(defaults.wallpaperHistory)
    override val dataRefreshFromNetworkEvent = mutablePreference(defaults.dataRefreshFromNetworkEvent)
    override val simulateNetworkFailure = mutablePreference(defaults.simulateNetworkFailure)
    override val reviewFeedItemDismissedTime = mutablePreference(defaults.reviewFeedItemDismissedTime)
    override val reviewFeedItemDismissedCount = mutablePreference(defaults.reviewFeedItemDismissedCount)
    override val inAppReviewShownOnce = mutablePreference(defaults.inAppReviewShownOnce)
    override val inAppReviewShownTime = mutablePreference(defaults.inAppReviewShownTime)
    override val doubleLicenseCheckFinished = mutablePreference(defaults.doubleLicenseCheckFinished)
    override val lastAppCloseTime = mutablePreference(defaults.lastAppCloseTime)
    override val lastWallpaperUsageTimeFuzzy = mutablePreference(defaults.lastWallpaperUsageTimeFuzzy)
    override val lastNotifyUserPurchasePendingOrderId = mutablePreferenceFlow(defaults.lastNotifyUserPurchasePendingOrderId)
    override val pendingPurchaseOrderId = mutablePreferenceFlow(defaults.pendingPurchaseOrderId)
    override val forceAds = mutablePreferenceFlow(defaults.adsEnabled)
    override val staggeredFeed = mutablePreferenceFlow(defaults.staggeredFeed)
    override val allowFullWidthFeedItems = mutablePreferenceFlow(defaults.allowFullWidthFeedItems)
    override val pagedIndexScreen = mutablePreferenceFlow(defaults.pagedIndexScreen)
    override val wallpaperScreenBottomSheet = mutablePreference(defaults.spotlightBottomSheet)
    override val wallpaperScreenShowsCollection = mutablePreference(defaults.spotlightShowsCollection)
    override val wallpaperScreenShowsOtherCollections = mutablePreference(defaults.spotlightShowsOtherCollections)
    override val wallpaperScreenTheme = mutableSetting(
        defaults.spotlightTheme, settings, updateDispatcher, coroutineScopeMain,
        mapper = { WallpaperScreenTheme.valueOf(it) },
        inverseMapper = { it.name },
    ).observeForProcessBridge()
    override val enableIosNativeNavigation = mutablePreferenceFlow(defaults.enableIosNativeNavigation)
    override val enableLogging = mutablePreferenceFlow(defaults.enableLogging)
    override val randomSeedIndex = mutablePreferenceFlow(defaults.randomSeedIndex)
    override val showPerformanceStats = mutablePreferenceFlow(defaults.showPerformanceStats)
    override val systemPhotoStatusCache = mutablePreferenceFlow(defaults.systemPhotoStatusCache)
    override val debugBillingData = mutablePreferenceFlow(defaults.debugBillingData)
    override val debugLicenseState = mutablePreference(defaults.debugLicenseState)
    override val useDebugBillingManager = mutablePreferenceFlow(defaults.useDebugBillingManager)
    override val useDebugRewardAdCount = mutablePreferenceFlow(defaults.useDebugRewardAdCount)
    override val enableNativeUiRendering = mutablePreferenceFlow(defaults.enableNativeUiRendering)
    override val purchasesRestoredForId = mutablePreferenceFlow(defaults.purchasesRestoredForId)
    override val imageFormatCodeInAppCompose = mutablePreferenceFlow(defaults.imageFormatCodeInAppCompose)
    override val enableFeedPaging = mutablePreferenceFlow(defaults.enableFeedPaging)
    override val recentlyViewedWallpaperIds = mutablePreferenceFlow(defaults.recentlyViewedWallpaperIds)
    override val recentlyViewedCollectionIds = mutablePreferenceFlow(defaults.recentlyViewedCollectionIds)
    override val recentlyViewedArtistIds = mutablePreferenceFlow(defaults.recentlyViewedArtistIds)
    override val enableNativeIosCollectionScreen = mutablePreferenceFlow(defaults.enableNativeIosCollectionScreen)

    override val lastExpirationEpochTimeForWhichUserNotified = mutablePreferenceFlow(defaults.lastExpirationEpochTimeForWhichUserNotified)
    override val lastExpiredSubscriptionNotifyCount = mutablePreferenceFlow(defaults.lastExpiredSubscriptionNotifyCount)
    override val lastRequestReviewEpochTime = mutablePreferenceFlow(defaults.lastRequestReviewEpochTime)

    private inline fun <reified T : Any> mutablePreferenceFlow(preferenceInfo: PreferenceInfo<T>): MutableStateFlow<T> {
        return mutableSettingFlow(preferenceInfo, settings, updateDispatcher, coroutineScopeMain)
//            .observeForProcessBridge() // TODO: Add alt-process support for Flow
    }

    private inline fun <reified T : Any> mutablePreference(preferenceInfo: PreferenceInfo<T>): MutableObservableValue<T> {
        return mutableSetting(preferenceInfo, settings, updateDispatcher, coroutineScopeMain)
            .observeForProcessBridge()
    }

    /**
     * Must be called for each [MutableObservableValue] instance so [processBridge] can be updated
     * with variable changes.
     */
    private fun <T : Any> MutableObservableValue<T>.observeForProcessBridge()
            : MutableObservableValue<T> {
        return processBridgePreferenceManager.observeForProcessBridge(this)
    }
}
