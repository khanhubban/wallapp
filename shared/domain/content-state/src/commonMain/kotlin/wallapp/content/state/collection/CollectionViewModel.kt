package wallapp.content.state.collection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import wallapp.app.AppStateManager
import wallapp.app.AppViewModelFactory
import wallapp.apphistory.AppHistoryManager
import wallapp.bottomsheet.BottomSheetViewStateProvider
import wallapp.bottomsheet.BottomSheetViewStateProviderFactory
import wallapp.bottomsheet.BottomSheetViewStateProviderManager
import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperId
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.downloadstatus.DownloadStatusViewState
import wallapp.content.state.error.permissionSystemMediaDenied
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.toolbar.CollapsingToolbarStateController
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.CollectionScreenContentResult
import wallapp.data.wallpaper.StaticWallpaperSize
import wallapp.graphics.Color
import wallapp.log.Logger
import wallapp.permission.SystemPermissionManager
import wallapp.permission.SystemPermissionType
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.pixel.util.ScrollDirection
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewVisibleState
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.CollectionActionScreenArgument
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.theme.customColorToken
import wallapp.util.combine
import wallapp.view.ViewEventFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel
import wallapp.wallpaper.download.ActiveWallpaperDownloadManager
import wallapp.worker.BackgroundWorkScheduler


class CollectionViewModel(
    collectionId: CollectionId,
    private val firstWallpaperId: WallpaperId?,
    contentRepository: ContentRepository,
    appHistoryManager: AppHistoryManager,
    activeWallpaperDownloadManager: ActiveWallpaperDownloadManager,
    private val backgroundWorkScheduler: BackgroundWorkScheduler,
    private val messageBarManager: MessageBarManager,
    private val viewStateFactory: ViewStateFactory,
    private val contentColorManager: ContentColorManager,
    private val viewEventFactory: ViewEventFactory,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val systemPermissionManager: SystemPermissionManager,
    private val appStateManager: AppStateManager,
    private val bottomSheetViewStateProviderManager: BottomSheetViewStateProviderManager,
    private val appViewModelFactory: AppViewModelFactory,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(),
    ScreenViewStateProvider,
    ScreenSystemBarControllerHolder,
    BottomSheetViewStateProviderFactory {

    companion object {
        val Log = Logger("[CollectionViewModel]")
    }

    private val topBarContainer: Color
        get() = contentColorManager.topBarContainer.value
    private val topBarContainerFlow: StateFlow<Color>
        get() = contentColorManager.topBarContainer

    private val scrollStateController = FeedScrollStateController(viewModelScope)
    private val collapsingToolbarStateController = CollapsingToolbarStateController(viewModelScope)
    private val persistableStateUpdateFlow = combine(
        scrollStateController.lastScrollStateUpdateFlow,
        collapsingToolbarStateController.lastCollapsingToolbarStateUpdateFlow,
    ) { scrollState, collapsingToolbarState ->
        scrollState to collapsingToolbarState
    }

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        topBarContainerFlow
            .map { ScreenSystemBarController.Dynamic(it) }
            .stateIn(
                ScreenSystemBarController.Dynamic(topBarContainer),
                startWhileSubscribedNetwork = true,
            )
    }

    private val downloadCollectionViewEventHandler = ViewEventHandler.createOnClick {
        val collection = data.value?.collectionState ?: return@createOnClick
        systemPermissionManager.permissionGuardedAction(
            SystemPermissionType.SystemMedia,
            actionOnSuccess = {
                backgroundWorkScheduler.scheduleWallpaperDownloads(
                    collection.wallpapers,
                    staticWallpaperSize = StaticWallpaperSize.FullResolution,
                    saveToGallery = true,
                )
            },
            actionOnDenied = {
                viewEventFactory.createNavigateToError(permissionSystemMediaDenied()).invoke()
            }
        )
    }

    private val contentPrefetcher: ContentPrefetcher by lazy {
        contentPrefetcherFactory.createContentPrefetcher(
            screenId = "Collection-${collectionId.name}",
            coroutineScope = viewModelScope,
        )
    }

    private val scrollDirectionChangedListener = { scrollDirection: ScrollDirection? ->
        scrollDirection?.let {
            if (scrollDirection == ScrollDirection.Descending) {
                toolbarExpandedToggle.value = false
            } else {
                toolbarExpandedToggle.value = true
            }
        }
    }

    private val viewsVisibleListener: ViewsVisibleListener by lazy {
        object : ViewsVisibleListener {
            override fun onScrollDirectionChanged(scrollDirection: ScrollDirection?) {
                contentPrefetcher.viewsVisibleListener.onScrollDirectionChanged(scrollDirection)
                scrollDirectionChangedListener(scrollDirection)
            }

            override fun onVisibleViewsChanged(visibleViews: List<ViewVisibleState>) {
                contentPrefetcher.viewsVisibleListener.onVisibleViewsChanged(visibleViews)
            }

            override fun onVisibleViewsSettled(visibleViews: List<ViewVisibleState>) {
                contentPrefetcher.viewsVisibleListener.onVisibleViewsSettled(visibleViews)
            }
        }
    }

    private val toolbarExpandedToggle = MutableStateFlow(true)

    private fun createViewState(
        isUiReady: Boolean,
        data: CollectionScreenContentResult?,
        messageBarViewState: MessageBarViewState? = null,
        topBarContainer: Color = this.topBarContainer,
        activeDownloadStatus: DownloadStatusViewState?,
        showAdFreeCollectionLockedInfo: Boolean,
    ): CollectionViewState {
        if (!isUiReady) {
            return CollectionViewState.Loading
        }
        
        return viewStateFactory.createCollectionViewState(
            data,
            messageBarViewState,
            topBarContainer.customColorToken,
            scrollStateController.scrollStateWrapper,
            collapsingToolbarStateController.collapsingToolbarStateWrapper,
            activeDownloadStatus,
            contentPrefetcher,
            downloadCollectionViewEventHandler,
            toolbarExpanded = toolbarExpandedToggle.value,
            showAdFreeCollectionLockedInfo = showAdFreeCollectionLockedInfo,
            viewsVisibleListener,
            firstWallpaperId,
        )
    }

    private val data: StateFlow<CollectionScreenContentResult?> = contentRepository
        .getCollectionScreenContent(collectionId, firstWallpaperId)
        .stateIn(null)

    override val viewState: StateFlow<CollectionViewState> = combine(
        appStateManager.isUiReady,
        data,
        messageBarManager.messageBarViewState,
        topBarContainerFlow,
        activeWallpaperDownloadManager.activeDownloadStatus,
        viewStateRefresher.refresh,
        persistableStateUpdateFlow,
        toolbarExpandedToggle,
    ) { isUiReady, data, messageBar, topBarContainer, activeDownloadStatus, _, _, _ ->
        createViewState(
            isUiReady = isUiReady,
            data = data,
            messageBarViewState = messageBar,
            topBarContainer = topBarContainer,
            activeDownloadStatus = activeDownloadStatus,
            showAdFreeCollectionLockedInfo = data?.collectionState?.showAdFreeCollectionLockedInfo ?: false,
        )
    }.stateIn(createViewState(isUiReady = false, data = null, activeDownloadStatus = null, showAdFreeCollectionLockedInfo = false))

    override fun createBottomSheetScreenViewStateProvider(screenArgument: ScreenArgument?):
            BottomSheetViewStateProvider {
        require(screenArgument is CollectionActionScreenArgument) {
            "Expected CollectionActionScreenArgument, got: $screenArgument"
        }
        return appViewModelFactory.createCollectionActionViewModel(screenArgument)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("onCleared")
        bottomSheetViewStateProviderManager.unregister(this)
    }

    init {
        Log.d("init")
        bottomSheetViewStateProviderManager.register(this)

        Log.d("init: $collectionId, firstWallpaperId: $firstWallpaperId")
        appHistoryManager.registerCollectionView(collectionId, null)
    }
}