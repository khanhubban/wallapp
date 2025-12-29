package wallapp.content.state.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import wallapp.content.model.Wallpaper
import wallapp.data.favorite.FavoriteItemsRepository
import wallapp.pixel.feed.FeedViewState
import wallapp.pixel.view.ViewsVisibleListener
import wallapp.view.ViewFactory
import wallapp.view.ViewSpecFactory
import wallapp.view.ViewStateFactory
import wallapp.view.ViewStateRefresher
import wallapp.view.feed.FeedFormatter
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerBottomNavigationBar
import wallapp.view.feed.FeedFormatter.Companion.FeedSpacerTopToolbar
import wallapp.viewmodel.ViewModel


class FavoritesViewModel(
    favoriteItemsRepository: FavoriteItemsRepository,
    viewStateRefresher: ViewStateRefresher,
    private val viewFactory: ViewFactory,
    private val viewStateFactory: ViewStateFactory,
    private val viewSpecFactory: ViewSpecFactory,
    private val viewsVisibleListener: ViewsVisibleListener,
    private val feedFormatter: FeedFormatter,
): ViewModel() {

    private val noDataViewState by lazy {
        viewStateFactory.createNoFavoritesViewState()
    }

    private fun createViewState(
        favorites: List<Wallpaper>?,
    ): FavoritesViewState {
        return when {
            favorites == null -> {
                FavoritesViewState.Loading
            }
            favorites.isEmpty() -> {
                FavoritesViewState.NoData(noDataViewState)
            }
            else -> {
                val feedViewSpec = viewSpecFactory.feedStaggeredViewSpec
                FavoritesViewState.Success(
                    feedViewState = FeedViewState(
                        feedViewSpec = feedViewSpec,
                        views = feedFormatter.format(
                            views = favorites.map {
                                viewFactory.createWallpaperFeedPreview(
                                    wallpaper = it,
                                    showPlusButton = false,
                                )
                            },
                            spacerStart = FeedSpacerTopToolbar,
                            spacerEnd = FeedSpacerBottomNavigationBar,
                            canInsertAds = true,
                        ),
                        viewsVisibleListener = viewsVisibleListener,
                    )
                )
            }
        }
    }

    private val recentFavorites = MutableStateFlow <List<Wallpaper>>(emptyList())

    /**
     * The favorites to display. This is the [currentFavorites], plus [recentFavorites].
     * This means if a user toggles a favorite, the item doesn't immediately dissapear from view.
     */
    private fun getDisplayFavorites(currentFavorites: List<Wallpaper>?): List<Wallpaper>? {
        if (currentFavorites == null) return null

        return mutableListOf<Wallpaper>()
            .apply {
                addAll(recentFavorites.value)
                addAll(currentFavorites)
            }
            .distinct()
            .also {
                recentFavorites.value = it
            }
    }

    val viewState: StateFlow<FavoritesViewState> = combine(
        favoriteItemsRepository.favoriteWallpapers,
        recentFavorites,
        viewStateRefresher.refresh,
    ) { favorites, _, _ ->
        createViewState(getDisplayFavorites(favorites))
    }.stateIn(createViewState(favorites = null), startWhileSubscribedNetwork = true)

    override fun onCleared() { }
}