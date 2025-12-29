package wallapp.data.favorite

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import wallapp.account.data.AccountDataRepository
import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.coroutine.CoroutineScopeIo
import wallapp.data.wallpaper.WallpaperRepository


class FavoriteItemsRepositoryDefault(
    private val accountDataRepository: AccountDataRepository,
    wallpaperRepository: WallpaperRepository,
    @CoroutineScopeIo private val scope: CoroutineScope,
) : FavoriteItemsRepository {

    private fun createFavoriteWallpaperItems(
        favorites: List<Id>? = null,
        wallpapers: List<Wallpaper> = emptyList(),
    ): List<Wallpaper>? {
        if (favorites == null) return null
        if (favorites.isEmpty()) return emptyList()

        return mutableListOf<Wallpaper>().apply {
            addAll(wallpapers.filter { favorites.contains(it.id) })
        }.ifEmpty { null }
    }

    override suspend fun toggleFavorite(id: Id): Boolean =
        accountDataRepository.setIsFavorite(id, !accountDataRepository.isFavorite(id))

    override fun isFavorite(id: Id): Boolean {
        return accountDataRepository.isFavorite(id)
    }

    override val favoriteWallpapers: Flow<List<Wallpaper>?> = combine(
        accountDataRepository.favoriteIds,
        wallpaperRepository.remixes,
    ) { favorites, remixes ->
        createFavoriteWallpaperItems(favorites, remixes)
    }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = createFavoriteWallpaperItems(),
        )
}
