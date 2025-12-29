package wallapp.content.state.showcase.ads

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import wallapp.data.content.ContentRepository
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.viewmodel.ViewModel

class ShowcaseAdsViewModel(
    contentRepository: ContentRepository,
    private val viewStateMapper: ShowcaseAdsViewStateMapper,
) : ViewModel(), ScreenViewStateProvider {

    override val viewState: StateFlow<ShowcaseAdsViewState> =
        contentRepository.exploreContent
            .map { viewStateMapper.mapShowcaseAdsViewState(it.wallpapers) }
            .stateIn(initialValue = ShowcaseAdsViewState.Loading)
}