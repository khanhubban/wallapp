package wallapp.pipeline.build

import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.manifest.PipelineManifest

object MediaMapBuilder {
    fun build(m: PipelineManifest): NetworkMediaData {
        val data = LinkedHashMap<Long, Map<String, String>>()
        fun url(path: String) = renditionUrl(m.baseUrl, path)

        for (w in m.wallpapers) {
            val dl = url(w.downloadRenditionPath)
            val pv = url(w.previewRenditionPath)
            data[mediaId("${w.id}:download")] = mapOf("dhd" to dl, "dsd" to dl)
            data[mediaId("${w.id}:preview")] = mapOf("s" to pv, "wfs" to pv, "wft" to pv, "fs" to pv)
        }
        val ap = url(m.artist.profileImagePath)
        data[mediaId("${m.artist.id}:profile")] = mapOf("am" to ap, "as" to ap, "e" to ap)
        data[mediaId("${m.folder.id}:profile")] = mapOf("wfs" to url(m.folder.profileImagePath))
        data[mediaId("${m.folder.id}:banner")] = mapOf("wfs" to url(m.folder.featureBannerImagePath))

        return NetworkMediaData(version = 1, mediaMap = data)
    }
}
