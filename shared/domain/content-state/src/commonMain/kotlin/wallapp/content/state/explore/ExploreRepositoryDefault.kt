package wallapp.content.state.explore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import wallapp.ad.AdManager
import wallapp.app.AppStateManager
import wallapp.appconfig.AppConfig
import wallapp.content.state.ContentState
import wallapp.content.state.ContentStateFeed
import wallapp.content.state.ContentStateMapper
import wallapp.content.state.paging.ContentStatePagingController
import wallapp.content.state.paging.ContentStatePagingControllerDefault
import wallapp.data.content.ContentRepository
import wallapp.view.ViewStateRefresher

class ExploreRepositoryDefault(
    contentRepository: ContentRepository,
    contentStateMapper: ContentStateMapper,
    private val appStateManager: AppStateManager,
    private val appConfig: AppConfig,
    viewStateRefresher: ViewStateRefresher,
    private val adManager: AdManager,
) : ExploreRepository {

    override val contentStateFeed: Flow<ContentStateFeed?> by lazy {
        combine(
            appStateManager.isUiReady,
            contentRepository.exploreContent,
            adManager.feedAdsEnabled,
            viewStateRefresher.refresh
        ) { isUiReady, data, feedAdsEnabled, _ ->
            if (!isUiReady) {
                return@combine null
            }
            if (feedAdsEnabled) {
                contentStateMapper.mapExploreForChunks(data)
            } else {
                contentStateMapper.mapExplore(data)
            }
        }.onEach {
            initPagingData(it)
        }
    }

    override val contentStatePagingController: ContentStatePagingController by lazy {
        ContentStatePagingControllerDefault(appConfig)
    }

    private fun initPagingData(data: ContentStateFeed?) {
        contentStatePagingController.initWithBackingContentStates {
            data?.feed?.filter { it is ContentState.Wallpaper || it is ContentState.Collection }
        }
    }
}