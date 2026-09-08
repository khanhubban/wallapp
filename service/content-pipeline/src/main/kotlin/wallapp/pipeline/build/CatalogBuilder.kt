package wallapp.pipeline.build

import wallapp.content.network.model.*
import wallapp.pipeline.manifest.PipelineManifest

object CatalogBuilder {
    fun build(m: PipelineManifest): NetworkContent {
        val artistId = m.artist.id
        val categoryId = "$artistId~singles"
        // Explore renders no collection rows without a Collection-type category, and
        // splitByCategoryType() treats any id NOT ending in "~singles" as one. Left free
        // (no purchasableProductIds) so it is never locked behind a store product.
        val collectionId = "$artistId~featured"

        val wallpapers = m.wallpapers.map { w ->
            val hd = mediaId("${w.id}:download")
            NetworkWallpaper(
                id = w.id,
                label = w.label,
                collectionLabel = "Singles",
                type = "parallax",
                artistId = artistId,
                wallpaperDownloadMedia = NetworkWallpaperDownloadMedia(
                    hdWidth = w.width, hdHeight = w.height, hdMediaId = hd, sdMediaId = hd,
                ),
                isDark = w.isDark,
                categoryId = categoryId,
                isSingle = true,
                previews = NetworkPreviews(standard = listOf(
                    NetworkMedia(id = mediaId("${w.id}:preview"), width = w.width, height = w.height),
                )),
                slugs = listOf("w/${w.id}"),
            )
        }

        val category = NetworkCategory(
            id = categoryId, label = "Singles", artistId = artistId,
            categoryType = "Singles",
            previewRemixId = wallpapers.first().id,
            remixIds = wallpapers.map { it.id },
            slugs = listOf(categoryId),
        )
        val collection = NetworkCategory(
            id = collectionId, label = "Featured", artistId = artistId,
            categoryType = "Collection",
            previewRemixId = wallpapers.first().id,
            remixIds = wallpapers.map { it.id },
            slugs = listOf("$artistId/featured"),
            purchasableProductIds = null,
        )
        val artist = NetworkArtist(
            id = artistId, label = m.artist.label,
            profileImage = NetworkMedia(id = mediaId("$artistId:profile")),
            slugs = listOf(artistId),
            categoryIds = listOf(categoryId, collectionId),
            socialLinks = NetworkSocialLinks(),
        )
        val folder = NetworkFolder(
            id = m.folder.id, title = m.folder.title,
            remixIds = wallpapers.map { it.id },
            profileImage = NetworkMedia(id = mediaId("${m.folder.id}:profile")),
            featureBannerImage = NetworkMedia(id = mediaId("${m.folder.id}:banner")),
        )
        return NetworkContent(wallpapers, listOf(category, collection), listOf(artist), listOf(folder))
    }
}
