package wallapp.appconfig

import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.content.model.WallpaperScreenTheme
import wallapp.flavorconfig.FlavorConfig
import wallapp.preference.MutableObservableValue
import wallapp.prefs.DevicePreferenceStorage
import wallapp.prefs.PreferenceDefaults


class AppConfigMainProcess(
    private val devicePreferenceStorage: DevicePreferenceStorage,
    private val preferenceDefaults: PreferenceDefaults,
    private val flavorConfig: FlavorConfig,
): AppConfig {

    override val defaultRemixId: String
        get() = preferenceDefaults.defaultRemixId.default()
    override val defaultDesignId: String
        get() = preferenceDefaults.defaultDesignId.default()
    override val allWallpapersUnlocked: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.allWallpapersUnlocked
    override val bucketOverride: MutableObservableValue<String>
        get() = devicePreferenceStorage.bucketOverride
    override val showTutorialOnFirstRun: Boolean
        get() = preferenceDefaults.preferenceDefaultsProvider.showTutorialOnFirstRun
    override val simulateNetworkFailure: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.simulateNetworkFailure
    override val adsEnabled: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.forceAds
    override val staggeredFeed: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.staggeredFeed
    override val allowFullWidthFeedItems: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.allowFullWidthFeedItems
    override val pagedIndexScreen: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.pagedIndexScreen
    override val spotlightBottomSheet: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.wallpaperScreenBottomSheet
    override val spotlightShowsCollection: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.wallpaperScreenShowsCollection
    override val spotlightShowsOtherCollections: MutableObservableValue<Boolean>
        get() = devicePreferenceStorage.wallpaperScreenShowsOtherCollections
    override val spotlightTheme: MutableObservableValue<WallpaperScreenTheme>
        get() = devicePreferenceStorage.wallpaperScreenTheme
    override val enableFeedPaging: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.enableFeedPaging
    override val showPerformanceStats: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.showPerformanceStats
    override val enableIosNativeNavigation: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.enableIosNativeNavigation
    override val enableLogging: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.enableLogging
    override val enableNativeUiRendering: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.enableNativeUiRendering
    override val enableNativeIosCollectionScreen: MutableStateFlow<Boolean>
        get() = devicePreferenceStorage.enableNativeIosCollectionScreen
}