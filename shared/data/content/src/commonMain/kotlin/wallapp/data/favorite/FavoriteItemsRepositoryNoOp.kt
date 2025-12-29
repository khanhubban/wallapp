package wallapp.data.favorite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import wallapp.content.model.Id
import wallapp.content.model.Wallpaper

object FavoriteItemsRepositoryNoOp : FavoriteItemsRepository {
    override val favoriteWallpapers: Flow<List<Wallpaper>?> = flowOf(null)

    override suspend fun toggleFavorite(id: Id): Boolean = false

    override fun isFavorite(id: Id): Boolean = false
}