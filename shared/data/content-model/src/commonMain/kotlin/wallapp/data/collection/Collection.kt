package wallapp.data.collection

import wallapp.content.model.Id.CollectionId
import wallapp.content.model.WallpaperRemix

data class Collection(
    val id: CollectionId,
    val wallpapers: List<WallpaperRemix>,
    val label: String,
)
