package wallapp.data.favorite

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id
import wallapp.content.model.Wallpaper

interface FavoriteItemsRepository {

    /**
     * null is the uninitialized state.
     */
    val favoriteWallpapers: Flow<List<Wallpaper>?>

    suspend fun toggleFavorite(id: Id): Boolean

    fun isFavorite(id: Id): Boolean
}