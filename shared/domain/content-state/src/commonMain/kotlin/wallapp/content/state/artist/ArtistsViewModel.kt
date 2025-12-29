package wallapp.content.state.artist

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import wallapp.content.state.colors.ContentColorManager
import wallapp.data.content.ContentRepository
import wallapp.data.content.ContentResult.ArtistsContentResult
import wallapp.graphics.Color
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.screen.ScreenSystemBarController
import wallapp.screen.ScreenSystemBarControllerHolder
import wallapp.theme.customColorToken
import wallapp.util.combine
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class ArtistsViewModel(
    contentRepository: ContentRepository,
    private val viewStateFactory: ViewStateFactory,
    private val contentColorManager: ContentColorManager,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider, ScreenSystemBarControllerHolder {

    private val topBarContainer: Color
        get() = contentColorManager.topBarContainer.value
    private val topBarContainerFlow: StateFlow<Color>
        get() = contentColorManager.topBarContainer

    override val screenSystemBarController: StateFlow<ScreenSystemBarController> by lazy {
        topBarContainerFlow
            .map { ScreenSystemBarController.Dynamic(it) }
            .stateIn(
                ScreenSystemBarController.Dynamic(topBarContainer),
                startWhileSubscribedNetwork = true,
            )
    }

    private fun createViewState(
        data: ArtistsContentResult?,
        topBarContainer: Color = this.topBarContainer,
    ): ArtistsViewState {
        return viewStateFactory.createArtistsViewState(data, topBarContainer.customColorToken)
    }

    override val viewState: StateFlow<ArtistsViewState> = combine(
        contentRepository.artistsContent,
        topBarContainerFlow,
        viewStateRefresher.refresh,
    ) { data, topBarContainer, _ ->
        createViewState(data, topBarContainer)
    }
        .stateIn(createViewState(null), startWhileSubscribedNetwork = true)
}