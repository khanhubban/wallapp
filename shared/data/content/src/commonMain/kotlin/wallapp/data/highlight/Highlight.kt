package wallapp.data.highlight

import wallapp.content.model.Id
import wallapp.content.model.Wallpaper
import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperCategoryType
import wallapp.data.artist.Artist
import wallapp.data.highlight.Highlight.ArtistHighlight
import wallapp.data.highlight.Highlight.CollectionHighlight
import wallapp.data.highlight.Highlight.FolderHighlight
import wallapp.data.highlight.Highlight.PlusHighlight
import wallapp.data.highlight.Highlight.SignInHighlight
import wallapp.data.highlight.Highlight.WallpaperHighlight
import wallapp.image.Image
import wallapp.string.quote
import wallapp.data.folder.Folder as FolderModel

sealed interface Highlight {

    val useDarkStatusBarIcons: Boolean

    data class CollectionHighlight(
        val category: WallpaperCategory,
        val previewWallpaper: Wallpaper,
        val label: String,
        override val useDarkStatusBarIcons: Boolean = false,
    ): Highlight {
        val collectionId: Id.CollectionId by lazy { category.id.collectionId }

        init {
            require(category.categoryType == WallpaperCategoryType.Collection) {
                "Category (${category.id.name.quote()}) must be a collection (categoryType: ${category.categoryType.name.quote()})"
            }
        }
    }

    data class FolderHighlight(
        val folder: FolderModel,
        val label: String,
        override val useDarkStatusBarIcons: Boolean = true,
    ): Highlight

    data class ArtistHighlight(
        val artist: Artist,
        val label: String,
        override val useDarkStatusBarIcons: Boolean = false,
    ): Highlight

    data class WallpaperHighlight(
        val wallpaper: Wallpaper,
        val label: String,
        override val useDarkStatusBarIcons: Boolean,
    ): Highlight

    data class PlusHighlight(
        val upgradePlusImage: Image,
        val label: String,
        override val useDarkStatusBarIcons: Boolean = false,
    ): Highlight

    data object SignInHighlight : Highlight {
        override val useDarkStatusBarIcons: Boolean = false
    }
}

val Highlight.debugString: String
    get() = when (this) {
        is ArtistHighlight -> "Artist: ${artist.id}"
        is CollectionHighlight -> "Collection: ${category.id}"
        is FolderHighlight -> "Folder: ${folder.id}"
        is PlusHighlight -> "Plus"
        is SignInHighlight -> "SignIn"
        is WallpaperHighlight -> "Wallpaper: ${wallpaper.id}"
    }