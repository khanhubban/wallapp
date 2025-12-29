package wallapp.data.content

import wallapp.content.model.WallpaperCategory
import wallapp.content.model.WallpaperItem
import wallapp.content.model.WallpaperRemix
import wallapp.data.artist.Artist
import wallapp.deeplink.DeepLinkMapping
import kotlin.jvm.JvmName

@JvmName("mapToDeepLinkMappingsArtist")
fun List<Artist>.mapToDeepLinkMappings(): List<DeepLinkMapping> {
    return map { artist ->
        DeepLinkMapping(
            artist.id,
            artist.slugs,
        )
    }
}

@JvmName("mapToDeepLinkMappingsWallpaperItem")
fun List<WallpaperItem>.mapToDeepLinkMappings(): List<DeepLinkMapping> {
    return mapNotNull { wallpaperItem ->
        when (wallpaperItem) {
            is WallpaperCategory -> {
                DeepLinkMapping(
                    wallpaperItem.id,
                    wallpaperItem.slugs,
                )
            }

            is WallpaperRemix -> {
                DeepLinkMapping(
                    wallpaperItem.id,
                    wallpaperItem.slugs,
                )
            }

            else -> {
                null
            }
        }
    }
}
