package wallapp.content.state.folder

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import wallapp.app.AppStateManager
import wallapp.content.model.Id.FolderId
import wallapp.content.prefetch.ContentPrefetcher
import wallapp.content.prefetch.ContentPrefetcherFactory
import wallapp.content.state.colors.ContentColorManager
import wallapp.content.state.feed.FeedScrollStateController
import wallapp.content.state.messagebar.MessageBarManager
import wallapp.content.state.toolbar.CollapsingToolbarStateController
import wallapp.data.folder.FolderState
import wallapp.data.folder.FolderStateRepository
import wallapp.graphics.Color
import wallapp.log.Logger
import wallapp.pixel.message.MessageBarViewState
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.theme.Theme
import wallapp.theme.ThemeManager
import wallapp.theme.customColorToken
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel


class FolderViewModel(
    folderId: FolderId,
    folderStateRepository: FolderStateRepository,
    appStateManager: AppStateManager,
    private val messageBarManager: MessageBarManager,
    private val viewStateFactory: ViewStateFactory,
    private val contentColorManager: ContentColorManager,
    private val contentPrefetcherFactory: ContentPrefetcherFactory,
    private val themeManager: ThemeManager,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(),
    ScreenViewStateProvider,
    ScreenSystemBarControllerHolder {

    companion object {
        val Log = Logger("FolderViewModel")
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
        MutableStateFlow(ScreenSystemBarController.DefaultOpposite).asStateFlow()
    }

    private val contentPrefetcher: ContentPrefetcher =
        contentPrefetcherFactory.createContentPrefetcher(
            screenId = "Folder-${folderId.name}",
            coroutineScope = viewModelScope,
        )

    private fun createViewState(
        isUiReady: Boolean,
        data: FolderState?,
        messageBarViewState: MessageBarViewState? = null,
        topBarContainer: Color = this.topBarContainer,
        oppositeTheme: Theme = themeManager.oppositeTheme.value,
    ): FolderViewState {
        if (!isUiReady) return FolderViewState.Loading

        return viewStateFactory.createFolderViewState(
            data = data,
            messageBarViewState = messageBarViewState,
            topBarContainerColor = topBarContainer.customColorToken,
            scrollStateWrapper = scrollStateController.scrollStateWrapper,
            contentPrefetcher = contentPrefetcher,
            theme = oppositeTheme,
        )
    }

    private val data: StateFlow<FolderState?> = folderStateRepository
        .getFolderState(folderId)
        .stateIn(null)

    override val viewState: StateFlow<FolderViewState> = combine(
        appStateManager.isUiReady,
        data,
        messageBarManager.messageBarViewState,
        topBarContainerFlow,
        viewStateRefresher.refresh,
        persistableStateUpdateFlow,
        themeManager.oppositeTheme,
    ) { isUiReady, data, messageBar, topBarContainer, _, _, oppositeTheme ->
        createViewState(isUiReady, data, messageBar, topBarContainer, oppositeTheme)
    }.stateIn(createViewState(isUiReady = false, data = null))
}