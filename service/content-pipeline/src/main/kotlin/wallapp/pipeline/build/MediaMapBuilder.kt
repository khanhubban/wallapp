package wallapp.pipeline.build

import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.manifest.PipelineManifest

object MediaMapBuilder {

    /**
     * A collection card stacks three preview layers, each looked up by its own SizedImage key
     * (see SizedImage.kt). Every wallpaper that a collection can show must therefore carry all
     * six, or the card renders as a broken image.
     */
    private val CollectionLayerKeys =
        listOf("wcs0", "wcs1", "wcs2", "wcl0", "wcl1", "wcl2")

    fun build(m: PipelineManifest): NetworkMediaData {
        val data = LinkedHashMap<Long, Map<String, String>>()
        fun url(path: String) = renditionUrl(m.baseUrl, path)

        for (w in m.wallpapers) {
            val dl = url(w.downloadRenditionPath)
            val pv = url(w.previewRenditionPath)
            data[mediaId("${w.id}:download")] = mapOf("dhd" to dl, "dsd" to dl)
            data[mediaId("${w.id}:preview")] = buildMap {
                put("s", pv); put("wfs", pv); put("wft", pv); put("fs", pv)
                CollectionLayerKeys.forEach { put(it, pv) }
            }
        }
        val ap = url(m.artist.profileImagePath)
        data[mediaId("${m.artist.id}:profile")] = mapOf("am" to ap, "as" to ap, "e" to ap)
        data[mediaId("${m.folder.id}:profile")] = mapOf("wfs" to url(m.folder.profileImagePath))
        // The folder's carousel highlight reads SizedImage.Exhibit ("e"); a feed key here leaves
        // the "Just Added" card with no background image.
        data[mediaId("${m.folder.id}:banner")] = mapOf("e" to url(m.folder.featureBannerImagePath))

        return NetworkMediaData(version = 1, mediaMap = data)
    }
}
