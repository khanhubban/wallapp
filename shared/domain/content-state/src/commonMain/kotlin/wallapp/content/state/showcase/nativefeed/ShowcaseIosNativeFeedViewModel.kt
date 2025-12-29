package wallapp.content.state.showcase.nativefeed

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import wallapp.data.content.ContentRepository
import wallapp.pixel.screen.ScreenViewStateProvider
import wallapp.view.ViewStateRefresher
import wallapp.viewmodel.ViewModel

class ShowcaseIosNativeFeedViewModel(
    contentRepository: ContentRepository,
    private val viewStateMapper: ShowcaseIosNativeFeedViewStateMapper,
    viewStateRefresher: ViewStateRefresher,
) : ViewModel(), ScreenViewStateProvider {

    override val viewState: StateFlow<ShowcaseIosNativeFeedViewState> = combine(
        contentRepository.exploreContent,
        viewStateRefresher.refresh
    ) { content, _ ->
            viewStateMapper.mapShowcaseAdsViewState(content.wallpapers)
    }
            .stateIn(initialValue = ShowcaseIosNativeFeedViewState.Loading)
}