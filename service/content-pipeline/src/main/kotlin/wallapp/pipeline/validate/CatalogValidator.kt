package wallapp.pipeline.validate

import kotlinx.serialization.json.Json
import wallapp.content.network.model.NetworkContent
import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.build.WireBundle
import wallapp.search.model.NetworkSearchMetadata

object CatalogValidator {
    private val strict = Json { ignoreUnknownKeys = true } // NOTE: NOT NetworkMediaData.fromJson (it swallows)

    fun validate(b: WireBundle) {
        val keys = b.media.mediaMap.keys

        // (a) every catalog-referenced media id is a key in the map; (b) required inner keys per role
        for (w in b.content.wallpapers) {
            val dl = w.wallpaperDownloadMedia.hdMediaId
            check(dl in keys) { "download media id $dl (${w.id}) missing from media map" }
            check(b.media.mediaMap[dl]!!.keys.containsAll(listOf("dhd", "dsd"))) { "download id $dl missing dhd/dsd" }
            check(w.wallpaperDownloadMedia.sdMediaId in keys) { "sd media id missing for ${w.id}" }
            for (p in w.previews.standard) {
                check(p.id in keys) { "preview media id ${p.id} (${w.id}) missing from media map" }
                check(b.media.mediaMap[p.id]!!.keys.containsAll(listOf("s", "wfs"))) { "preview id ${p.id} missing s/wfs" }
            }
        }
        for (a in b.content.artists) check(a.profileImage.id in keys) { "artist ${a.id} profile image missing from media map" }
        for (f in b.content.folders) {
            check(f.profileImage.id in keys) { "folder ${f.id} profile image missing" }
            check(f.featureBannerImage.id in keys) { "folder ${f.id} banner image missing" }
        }

        // (c) content ↔ search id-set equality (valid; content↔media is a different id space)
        val contentIds = b.content.wallpapers.map { it.id }.toSet()
        val searchIds = b.search.remixMetadata.map { it.remixId }.toSet()
        check(contentIds == searchIds) { "content wallpaper ids != search remix ids: ${contentIds - searchIds} / ${searchIds - contentIds}" }

        // (d) imgix pass-through invariant
        for ((_, map) in b.media.mediaMap) for ((_, url) in map)
            check(!url.startsWith(b.imgixHostPrefix)) { "rendition url on imgix host would be rewritten: $url" }

        // (e) strict re-decode of every wire file (media strictly, NOT via swallowing fromJson)
        check(NetworkContent.fromExportString(b.content.exportString).wallpapers.size == b.content.wallpapers.size)
        check(NetworkSearchMetadata.fromExportString(b.search.exportString).remixMetadata.size == b.search.remixMetadata.size)
        val mediaDecoded = strict.decodeFromString<NetworkMediaData>(b.media.exportString)
        check(mediaDecoded.mediaMap.size == b.media.mediaMap.size) { "media strict decode size mismatch" }
    }
}
