package wallapp.deeplink

import wallapp.content.model.Id
import wallapp.content.model.Id.FolderId
import wallapp.screen.ScreenArgument
import wallapp.screen.ScreenArgument.ArtistIdScreenArgument
import wallapp.screen.ScreenArgument.CollectionIdScreenArgument
import wallapp.screen.ScreenArgument.FolderScreenArgument
import wallapp.screen.ScreenArgument.WallpaperShowcaseScreenArgument


val DeepLinkMapping.toScreenArgument: ScreenArgument?
    get() = id.toScreenArgument

val Id.toScreenArgument: ScreenArgument?
    get() = when (this) {
        is Id.ArtistId -> {
            ArtistIdScreenArgument(this)
        }

        is Id.CategoryId -> {
            CollectionIdScreenArgument(this.collectionId, firstWallpaperId = null)
        }

        is Id.CollectionId -> {
            CollectionIdScreenArgument(this, firstWallpaperId = null)
        }

        is FolderId -> {
            FolderScreenArgument(this)
        }

        is Id.RemixId -> {
            WallpaperShowcaseScreenArgument(this)
        }

        else -> null

    }