package wallapp.data.util

import wallapp.content.model.Id
import wallapp.content.model.WallpaperAnimatedImageLayer
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperImageLayer
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperLayer
import wallapp.content.model.WallpaperRemix
import wallapp.image.Image

val List<WallpaperItem>.allRemixes: List<WallpaperRemix>?
    get() = filterIsInstance<WallpaperRemix>().ifEmpty { null }

val List<WallpaperItem>.allCategories: List<WallpaperCategory>?
    get() = filterIsInstance<WallpaperCategory>().ifEmpty { null }

val WallpaperLayer.image: Image?
    get() = when (this) {
        is WallpaperImageLayer -> image
        is WallpaperAnimatedImageLayer -> image
//        is WallpaperAnimatedLayer -> image
        else -> null
    }

/**
 * Returns all [WallpaperItem]s that are in the given [collectionIds]. The returned list will
 * include the [WallpaperCategory]s, [WallpaperDesign]s, and [WallpaperRemix]es.
 *
 * [this]: All wallpaper items (categories, designs, remixes)
 * [collectionIds]: The collection ids to filter by
 */
fun List<WallpaperItem>.filterCollectionHierarchyItems(
    collectionIds: List<Id.CollectionId>,
): List<WallpaperItem> {
    val collections = filterIsInstance<WallpaperCategory>()
        .filter { collectionIds.contains(it.id.collectionId) }

    val remixIds = collections.flatMap { it.remixIds }

    val remixes = filterIsInstance<WallpaperRemix>()
        .filter { remixIds.contains(it.id) }

    return collections + remixes
}
