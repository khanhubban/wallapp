package wallapp.pipeline.build

import wallapp.image.sized.MediaEntityKind
import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.manifest.PipelineManifest

object MediaMapBuilder {

    fun build(m: PipelineManifest): NetworkMediaData {
        val data = LinkedHashMap<Long, Map<String, String>>()
        fun url(path: String) = renditionUrl(m.baseUrl, path)
        fun entry(kind: MediaEntityKind, url: String): Map<String, String> =
            kind.requiredKeyStrings.associateWithTo(LinkedHashMap()) { url }

        for (w in m.wallpapers) {
            data[mediaId("${w.id}:download")] =
                entry(MediaEntityKind.WallpaperDownload, url(w.downloadRenditionPath))
            data[mediaId("${w.id}:preview")] =
                entry(MediaEntityKind.WallpaperPreview, url(w.previewRenditionPath))
        }
        data[mediaId("${m.artist.id}:profile")] =
            entry(MediaEntityKind.ArtistProfile, url(m.artist.profileImagePath))
        data[mediaId("${m.folder.id}:profile")] =
            entry(MediaEntityKind.FolderProfile, url(m.folder.profileImagePath))
        data[mediaId("${m.folder.id}:banner")] =
            entry(MediaEntityKind.FolderBanner, url(m.folder.featureBannerImagePath))

        return NetworkMediaData(version = 1, mediaMap = data)
    }
}
