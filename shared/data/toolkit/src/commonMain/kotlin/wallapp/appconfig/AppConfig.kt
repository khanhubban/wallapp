package wallapp.appconfig

import co.touchlab.skie.configuration.annotations.FlowInterop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wallapp.content.model.WallpaperScreenTheme
import wallapp.pixel.paging.PAGING_DEFAULT_PAGE_SIZE
import wallapp.pixel.paging.PAGING_INFINITE_SIZE
import wallapp.preference.MutableObservableValue
import wallapp.system.platform.PlatformFeature

interface AppConfig {

    val defaultRemixId: String

    val defaultDesignId: String

    val allWallpapersUnlocked: MutableObservableValue<Boolean>

    val bucketOverride: MutableObservableValue<String>

    val showTutorialOnFirstRun: Boolean

    val simulateNetworkFailure: MutableObservableValue<Boolean>

    val adsEnabled: MutableStateFlow<Boolean>
    val staggeredFeed: MutableStateFlow<Boolean>
    val allowFullWidthFeedItems: StateFlow<Boolean>
    val pagedIndexScreen: StateFlow<Boolean>

    val spotlightBottomSheet: MutableObservableValue<Boolean>
    val spotlightShowsCollection: MutableObservableValue<Boolean>
    val spotlightShowsOtherCollections: MutableObservableValue<Boolean>
    val spotlightTheme: MutableObservableValue<WallpaperScreenTheme>

    val enableFeedPaging: MutableStateFlow<Boolean>
    val feedPagingDefaultPageSize: Int
        get() = if (enableFeedPaging.value) {
            PAGING_DEFAULT_PAGE_SIZE
        } else {
            PAGING_INFINITE_SIZE
        }

    val alwaysShowDownloadedToPhotos: Boolean
        get() = !PlatformFeature.CanOpenToSystemPhotosApp

    @FlowInterop.Enabled
    val showPerformanceStats: MutableStateFlow<Boolean>
    @FlowInterop.Enabled
    val enableIosNativeNavigation: MutableStateFlow<Boolean>
    @FlowInterop.Enabled
    val enableLogging: MutableStateFlow<Boolean>
    @FlowInterop.Enabled
    val enableNativeUiRendering: MutableStateFlow<Boolean>
    @FlowInterop.Enabled
    val enableNativeIosCollectionScreen: MutableStateFlow<Boolean>

    val searchIsOverlay: Boolean
        get() = true
}