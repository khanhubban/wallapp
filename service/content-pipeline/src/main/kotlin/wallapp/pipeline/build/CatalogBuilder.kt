package wallapp.pipeline.build

import wallapp.content.network.model.*
import wallapp.pipeline.manifest.PipelineManifest

object CatalogBuilder {
    fun build(m: PipelineManifest): NetworkContent {
        val artistId = m.artist.id
        val categoryId = "$artistId~singles"

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
        val artist = NetworkArtist(
            id = artistId, label = m.artist.label,
            profileImage = NetworkMedia(id = mediaId("$artistId:profile")),
            slugs = listOf(artistId),
            categoryIds = listOf(categoryId),
            socialLinks = NetworkSocialLinks(),
        )
        val folder = NetworkFolder(
            id = m.folder.id, title = m.folder.title,
            remixIds = wallpapers.map { it.id },
            profileImage = NetworkMedia(id = mediaId("${m.folder.id}:profile")),
            featureBannerImage = NetworkMedia(id = mediaId("${m.folder.id}:banner")),
        )
        return NetworkContent(wallpapers, listOf(category), listOf(artist), listOf(folder))
    }
}
