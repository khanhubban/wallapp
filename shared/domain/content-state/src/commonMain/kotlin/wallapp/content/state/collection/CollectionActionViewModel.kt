package wallapp.content.state.collection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import wallapp.bottomsheet.BottomSheetViewStateProvider
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperId
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.data.collection.CollectionState
import wallapp.data.content.ContentRepository
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.log.Logger
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionType
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewEventSink
import wallapp.purchase.PurchaseManager
import wallapp.resources.string.StringRepository
import wallapp.screen.ScreenArgument.CollectionActionScreenArgument
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.view.menu.MenuItemFactory
import wallapp.viewmodel.ViewModel
import wallapp.wallpaper.actionbutton.CollectionActionButtonManager
import wallapp.wallpaper.actionbutton.CollectionActionButtonState
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager
import wallapp.worker.BackgroundWorkScheduler

class CollectionActionViewModel(
    private val argument: CollectionActionScreenArgument,
    activeWallpaperDownloadManager: ActiveWallpaperDownloadManager,
    private val backgroundWorkScheduler: BackgroundWorkScheduler,
    contentRepository: ContentRepository,
    private val menuItemFactory: MenuItemFactory,
    private val purchaseManager: PurchaseManager,
    collectionActionButtonManager: CollectionActionButtonManager,
    private val systemPermissionManager: SystemPermissionManager,
    private val viewStateFactory: ViewStateFactory,
    private val viewEventFactory: ViewEventFactory,
    private val strings: StringRepository,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), BottomSheetViewStateProvider {

    companion object {
        val Log = Logger("WallpaperActionViewModel")
    }

    private val collectionId: CollectionId
        get() = argument.collectionId
    private val singleWallpaperId: WallpaperId?
        get() = argument.singleWallpaperId

    private val eventSink: (CollectionActionViewEvent) -> Unit = { event ->
        when (event) {
            is CollectionActionViewEvent.BuyCollection -> {
                viewModelScope.launch {
                    val collectionState = collectionState.first() ?: return@launch
                    navigateToBuyCollection(collectionState)
                }
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    private val buyCollectionEventHandler = ViewEventHandler.Event(
        eventSink = eventSink as ViewEventSink,
        event = CollectionActionViewEvent.BuyCollection,
    )

    private val collectionState: StateFlow<CollectionState?> =
        contentRepository.getCollectionState(collectionId)
            .stateIn(null)

    private val collectionActionButtonState: Flow<CollectionActionButtonState> =
        collectionActionButtonManager.getCollectionActionButtonState(
            singleWallpaperId,
            collectionState,
            staticWallpaperSize = StaticWallpaperSize.FullResolution,
        )

    private val downloadCollectionViewEventHandler = ViewEventHandler.createOnClick {
        val collectionState = collectionState.value ?: return@createOnClick
        systemPermissionManager.permissionGuardedAction(
            SystemPermissionType.SystemMedia,
            actionOnSuccess = {
                backgroundWorkScheduler.scheduleWallpaperDownloads(
                    collectionState.wallpapers,
                    staticWallpaperSize = StaticWallpaperSize.FullResolution,
                    saveToGallery = true,
                )
            },
            actionOnDenied = {
                viewEventFactory.createNavigateToError(permissionSystemMediaDenied()).invoke()
            }
        )
    }


    @Suppress("UNUSED_PARAMETER")
    private fun createViewState(
        collectionState: CollectionState? = null,
        activeDownloadStatus: DownloadStatusViewState? = null,
        collectionActionButtonState: CollectionActionButtonState? = null,
    ): CollectionActionViewState {

        val actionButtons = createActionButtons(collectionState, collectionActionButtonState, activeDownloadStatus)

        return viewStateFactory.createCollectionActionViewState(
            collectionState = collectionState,
            actionButton1 = actionButtons.first,
            actionButton2 = actionButtons.second,
            showAdFreeCollectionLockedInfo = collectionState?.showAdFreeCollectionLockedInfo ?: false,
        )
    }

    override val viewState: StateFlow<CollectionActionViewState> = combine(
        collectionState,
        collectionActionButtonState,
        activeWallpaperDownloadManager.activeDownloadStatus,
        viewStateRefresher.refresh,
    ) { collection, collectionActionButtonState, activeDownloadStatus, _ ->
        createViewState(collection, activeDownloadStatus, collectionActionButtonState)
    }.stateIn(
        initialValue = createViewState(),
    )

    override fun destroy() {
        onCleared()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("onCleared")
    }

    private fun navigateToBuyCollection(collectionState: CollectionState) {
        viewModelScope.launch {
            purchaseManager.getPurchasableCollection(collectionState.id).firstOrNull()?.also {
                viewEventFactory.createOnClickInitiatePurchase(
                    purchasable = it,
                    isSubscription = false,
                ).invoke()
            }
        }
    }

    private fun createActionButtons(
        collectionState: CollectionState?,
        collectionActionButtonState: CollectionActionButtonState?,
        activeDownloadStatus: DownloadStatusViewState?,
    ): Pair<MenuItem, MenuItem?> {
        val isUnlocked = collectionState?.connectionState?.isUnlocked ?: false
        val priceLabel = collectionState?.priceLabel.orEmpty()

        return if (isUnlocked) {
            val downloadAllButton = menuItemFactory.createDownloadAllButton(
                downloadCollectionViewEventHandler = downloadCollectionViewEventHandler,
                activeDownloadStatus = activeDownloadStatus,
            )
            val actionButton2 = if (singleWallpaperId != null) {
                collectionActionButtonState?.buttonViewState
            } else {
                null
            }

            downloadAllButton to actionButton2
        } else {
            val plusButton = menuItemFactory.createPlusHeroButton(label = strings.unlockAllCollections)
            val buyCollectionButton = menuItemFactory.createBuyCollectionButton(
                priceLabel,
                eventHandler = buyCollectionEventHandler,
            )
            buyCollectionButton to plusButton
        }
    }

    init {
        Log.d("init: $argument")
    }
}