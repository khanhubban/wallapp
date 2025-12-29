package wallapp.wallpaper.live

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import wallapp.annotation.VisibleForTesting
import wallapp.appconfig.AppPlatformConfig
import wallapp.appstate.AppState
import wallapp.content.model.Id.RemixId
import wallapp.content.model.WallpaperCategoryWithRemixes
import wallapp.content.model.WallpaperDefaults
import wallapp.content.model.WallpaperRemix
import wallapp.coroutine.collectIn
import wallapp.data.wallpaper.WallpaperRepository
import wallapp.flavorconfig.FlavorConfig
import wallapp.log.Log
import wallapp.preference.CompositeSubscription
import wallapp.preferences.UserPreferences
import wallapp.process.Process
import wallapp.process.isLiveWallpaperProcess
import wallapp.system.toast.ToastDisplayController
import wallapp.wallpaper.app.AppWallpaperManager
import wallapp.wallpaper.history.WallpaperHistoryManager
import wallapp.wallpaper.history.WallpaperHistoryManagerDefault
import wallapp.wallpaper.static.StaticWallpaperManager


class LiveWallpaperManagerKmm(
    private val appWallpaperManager: AppWallpaperManager,
    private val staticWallpaperManager: StaticWallpaperManager,
    private val userPreferences: UserPreferences,
    private val wallpaperRepository: WallpaperRepository,
    private val wallpaperDefaults: WallpaperDefaults,
    appState: AppState,
    private val appPlatformConfig: AppPlatformConfig,
    private val toastDisplayController: ToastDisplayController,
    private val process: Process,
    private val flavorConfig: FlavorConfig,
    coroutineScopeIo: CoroutineScope,
) : LiveWallpaperManager {

    override val isLiveWallpaperAvailable: Boolean
        get() = appPlatformConfig.isLiveWallpaperAvailable
    val isSetStaticWallpaperAvailable: Boolean
        get() = staticWallpaperManager.canSetWallpaper

    override val isReady = MutableStateFlow(false)

    override val historyManager: WallpaperHistoryManager = WallpaperHistoryManagerDefault(appState)

//    private val designAndRemixResult = getAllDesignsWithWallpapersUseCase.observe()
    private var wallpaperRemixes: List<WallpaperRemix>? = null
    private var categoriesWithRemixes: List<WallpaperCategoryWithRemixes>? = null

    override val currentWallpaper = MutableStateFlow<WallpaperRemix?>(null)
    override fun getCurrentWallpaper(): WallpaperRemix? = currentWallpaper.value

    override val nextWallpaper = MutableStateFlow<WallpaperRemix?>(null)

    private val subscriptions = CompositeSubscription()

    private var lastKnownDarkMode: Boolean? = null

    private val allWallpaperRemixes: List<WallpaperRemix>
        get() = wallpaperRemixes ?: emptyList()

    private var refetchAndSetCurrentWallpaper = false

    private fun onDataUpdatedSuccess(data: List<WallpaperCategoryWithRemixes>) {
        categoriesWithRemixes = data
        wallpaperRemixes = data.flatMap { it.remixes }

        // Don't set subscriptions until we have data. Prevents setNewCurrentWallpaper() resetting
        // wallpaper.
        if (data.isEmpty()) return

        if (!subscriptionsAdded) {
            subscriptions.addAll(
                userPreferences.currentRemixId.subscribe(skipFirst = false) { id ->
                    setNewCurrentWallpaper(id)
                },
                userPreferences.nextRemixId.subscribe(skipFirst = false) { id ->
                    allWallpaperRemixes.find { it.id == RemixId(id) }?.let {
                        Log.i("[wallpaperId] _nextWallpaper.updateValueIfNew(): id: ${it.id}")
                        nextWallpaper.value = it
                    }
                },
                //                    userPreferences.currentPreviewRemixId.subscribe(skipFirst = false) { id ->
                //                        updateWallpaperPreview(WallpaperRemixId(id))
                //                    }
            )
            subscriptionsAdded = true
            if (lastKnownDarkMode != null) {
                checkSwitchToDarkRemix()
                lastKnownDarkMode = null
            }
        }

        if (refetchAndSetCurrentWallpaper) {
            resetCurrentWallpaper()
        }

        isReady.value = true
    }


    private var subscriptionsAdded = false

    private var pendingTutorialDefaultDesign = false

    @VisibleForTesting var presetLightToDarkMap = mapOf(
        "spinner_32_color_1" to "spinner_32_color_2",
        "spinner_32_color_3" to "spinner_32_color_4",
        "spinner_32_color_5" to "spinner_32_color_6",
        "spinner_32_color_14" to "spinner_32_color_15",
        "spinner_32_color_7" to "spinner_32_color_8",
        "spinner_32_color_9" to "spinner_32_color_10",
        "spinner_32_color_11" to "spinner_32_color_12"
    )
    @VisibleForTesting var presetDarkToLightMap = presetLightToDarkMap.entries.associateBy({ it.value }) { it.key }

    init {
        // Ensure default values are set. See #272.
        if (userPreferences.currentRemixId.value == "0") {
            val defaultRemixId = wallpaperDefaults.defaultRemixId.name
            Log.d("Set default wallpaper asset id: $defaultRemixId")
            userPreferences.currentRemixId.updateIfNew(defaultRemixId)
            userPreferences.currentDesignId.updateIfNew(wallpaperDefaults.defaultDesignId.name)
        }
        if (userPreferences.currentPreviewRemixId.value == "0") {
            val defaultRemixId = wallpaperDefaults.defaultRemixId.name
            Log.d("Set default preview wallpaper asset id: $defaultRemixId")
            userPreferences.currentPreviewRemixId.updateIfNew(defaultRemixId)
        }

        wallpaperRepository.categoriesWithRemixes.collectIn(coroutineScopeIo) {
            onDataUpdatedSuccess(it)
        }

        currentWallpaper.collectIn(coroutineScopeIo) {
            if (it != null) {
                historyManager.onWallpaperChanged(it)
            }
        }

        when {
            process.isLiveWallpaperProcess() -> {
                wallpaperRepository.refreshCounter.collectIn(coroutineScopeIo) {
                    Log.d("[datarefresh] received dataRefreshedFromDb event, process=%s", process.processNameSuffix)
//                        resetWallpaperRemixes()
                }
            }
            process.isDefaultProcess -> {
                appState.dataRefreshFromNetworkEvent.subscribe {
                    Log.d("[datarefresh] received dataRefreshFromNetworkEvent, process=%s", process.processNameSuffix)
//                    resetWallpaperRemixes()
                }
            }
        }
    }

    private fun setNewCurrentWallpaper(id: String) {
//        if (!dataRefreshedFromNetwork && id != wallpaperDefaults.defaultRemixId.name) return
        val remix = findWallpaperRemix(RemixId(id))
        if (remix != null) {
            Log.i("[wallpaperId] _currentWallpaper.updateValueIfNew(): id: ${remix.id}")
            currentWallpaper.value = remix
        } else {
            // TODO: Add support using categories / remixes
//            val currentDesign = wallpaperDesignResults?.find {
//                it.design.id.name == userPreferences.currentDesignId.value
//            }?.design
//            if (currentDesign != null) {
//                updateCurrentWallpaper(
//                    currentDesign.previewRemix,
//                    null,
//                    "Changing to preview of same design that the deleted remix belonged to"
//                )
//                currentWallpaper.value = currentDesign.previewRemix
//            } else {
//                historyManager.clearHistory()
//                // the current wallpaper has gone completely so...
//                findAndSetBestAvailableWallpaper()
//            }
        }
    }

    private fun findAndSetBestAvailableWallpaper() {
        val setDefaultRemix = {
            setDefaultWallpaper("Designs observer")
            findWallpaperRemix(wallpaperDefaults.defaultRemixId)?.let {
                currentWallpaper.value = it
            }
        }
        setDefaultRemix()
    }

    private fun resetCurrentWallpaper() {
        currentWallpaper.value?.id?.also {
            Log.d(
                "[datarefresh] setting to %s after resetting, process=%s",
                it.name,
                process.processNameSuffix
            )
            findWallpaperRemix(it)?.let { remix ->
                val previousWallpaper = currentWallpaper.value
                Log.d("$previousWallpaper")
                appWallpaperManager.updateCurrentDisplayWallpaper(remix.id)
//                _currentWallpaperPreview.updateValueIfNew(remix)
                currentWallpaper.value = remix
            }
        }
        refetchAndSetCurrentWallpaper = false
    }

//    private fun resetWallpaperRemixes() {
//        subscriptions.cancel()
//        subscriptionsAdded = false
//        refetchAndSetCurrentWallpaper = true
////        getAllDesignsWithWallpapersUseCase(Unit)
//    }

    override fun updateCurrentWallpaper(
        remixId: RemixId,
        nextRemixId: RemixId?,
        reason: String, /* for logging purposes */
        setAsStatic: Boolean,
        showToast: Boolean,
        forceShowDebugNotification: Boolean,
    ) {
        findWallpaperRemix(remixId)?.let {
            updateCurrentWallpaper(
                wallpaperRemix = it,
                nextRemixId = nextRemixId,
                reason = reason,
                setAsStatic = setAsStatic,
                showToast = showToast,
                forceShowDebugNotification = forceShowDebugNotification
            )
        }
    }

    override fun updateCurrentWallpaper(
        wallpaperRemix: WallpaperRemix,
        nextRemixId: RemixId?,
        reason: String, /* for logging purposes */
        setAsStatic: Boolean,
        showToast: Boolean,
        forceShowDebugNotification: Boolean,
    ) {
        Log.d("[wallpaperId] updateCurrentWallpaper(): id: ${wallpaperRemix.id}, reason: $reason")
        userPreferences.currentRemixId.updateIfNew(wallpaperRemix.id.name)
//        userPreferences.currentDesignId.updateIfNew(wallpaperRemix.designId.name)

        if (!isSetStaticWallpaperAvailable && !isLiveWallpaperAvailable) {
            return
        }

        if (setAsStatic) {
            TODO("Re-implement using new StaticWallpaperManager API")
//            val staticWallpaperSize = if (wallpaperRemix.staticImageMatchesScreenSize) {
//                StaticWallpaperSize.MatchScreenSize
//            } else {
//                StaticWallpaperSize.MatchScreenSizeWithPadding
//            }
//            staticWallpaperManager.setWallpaper(
//                wallpaperRemix,
//                staticWallpaperSize = staticWallpaperSize,
//            )
        } else {
            updateNextWallpaper(nextRemixId)
        }

        if (showToast) toastDisplayController.showToast(wallpaperRemix.label, cancelPrevious = true)
    }

    override fun revertWallpaperChange() {
        val previousWallpaperId = historyManager.popPreviousRemixOrWallpaper() ?: return
        historyManager.popCurrent()
        updateCurrentWallpaper(previousWallpaperId, null, "revertWallpaperChange")
    }

    override fun updateNextWallpaper(remixId: RemixId?) {
        if (remixId != null) {
            Log.d("[wallpaperId] updateNextWallpaper(): id: $remixId")
            userPreferences.nextRemixId.updateIfNew(remixId.name)
        }
    }

    override fun useNextWallpaper(setNewRandomNextWallpaper: Boolean) {
        val nextWallpaperId = nextWallpaper.value ?: run {
            wallpaperRemixes?.random()
        }
        nextWallpaperId?.let {
            updateCurrentWallpaper(it,
                if (setNewRandomNextWallpaper) {
                    wallpaperRemixes?.random()?.id
                } else {
                    null
                }, "Next wallpaper tile")
        }
    }

    override fun checkSwitchToDarkRemix(alsoSwitchPreview: Boolean): Boolean {
        return false
    }

    private fun findWallpaperRemix(remixId: RemixId): WallpaperRemix? {
        return allWallpaperRemixes.find { it.id == remixId }
//            ?: wallpaperRepository.getDevicePhotoRemix(remixId.name)
    }

    override fun setDefaultWallpaper(message: String) {
        updateCurrentWallpaper(wallpaperDefaults.defaultRemixId, null,
            "Setting default wallpaper: $message")
    }
}
