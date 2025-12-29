package wallapp.data.wallpaper

import kotlinx.coroutines.flow.Flow
import wallapp.content.model.Id
import wallapp.content.model.Id.CategoryId
import wallapp.content.model.Id.RemixId
import wallapp.content.model.Ids
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryWithRemixes
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix

interface WallpaperRepository {

    val allWallpaperItems: Flow<List<WallpaperItem>>

    val allWallpapers: Flow<List<Wallpaper>>
    val allSingles: Flow<List<Wallpaper>>
    val allTracks: Flow<List<Wallpaper>>

    val remixes: Flow<List<WallpaperRemix>>
    fun getRemix(remixId: RemixId): Flow<WallpaperRemix?>
    fun getRemixes(remixIds: List<RemixId>): Flow<List<WallpaperRemix>?>
    fun getRemixesForCategory(categoryId: CategoryId): Flow<List<WallpaperRemix>?>

    val categories: Flow<List<WallpaperCategory>>
    val collectionCategories: Flow<List<WallpaperCategory>>
    fun getCategory(categoryId: CategoryId): Flow<WallpaperCategory?>
    fun getCategories(categoryIds: List<CategoryId>): Flow<List<WallpaperCategory>?>

    val categoriesWithRemixes: Flow<List<WallpaperCategoryWithRemixes>>

    fun getItem(id: Id): Flow<WallpaperItem?>
    /**
     * Return all items for given [ids].
     *
     * Results are:
     *      * Not guaranteed to be returned in the order of ids.
     *      * Number of results may be less than [ids.size].
     */
    fun getItems(ids: Ids): Flow<List<WallpaperItem>?>

    /**
     * Increments each time the data is refreshed. This is a hacky way to force a refresh during
     * the compatibility phase.
     */
    val refreshCounter: Flow<Int>
}
