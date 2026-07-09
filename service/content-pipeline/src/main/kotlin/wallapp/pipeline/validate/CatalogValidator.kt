package wallapp.pipeline.validate

import kotlinx.serialization.json.Json
import wallapp.content.network.model.NetworkContent
import wallapp.image.sized.MediaEntityKind
import wallapp.image.sized.SizedImage
import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.build.WireBundle
import wallapp.search.model.NetworkSearchMetadata

object CatalogValidator {
    private val strict = Json { ignoreUnknownKeys = true } // NOTE: NOT NetworkMediaData.fromJson (it swallows)

    fun validate(b: WireBundle) {
        val keys = b.media.mediaMap.keys

        // (a) per-kind key sets, derived from the one shared contract rather than a local copy.
        // Tripwire: under the current builder this confirms the enum equals itself. It still
        // catches a hand-edited catalog, an older builder's output, or a derivation regression.
        for (w in b.content.wallpapers) {
            val dl = w.wallpaperDownloadMedia.hdMediaId
            check(dl in keys) { "download media id $dl (${w.id}) missing from media map" }
            check(b.media.mediaMap[dl]!!.keys == MediaEntityKind.WallpaperDownload.requiredKeyStrings) {
                "download id $dl keys ${b.media.mediaMap[dl]!!.keys} != ${MediaEntityKind.WallpaperDownload.requiredKeyStrings}"
            }
            check(w.wallpaperDownloadMedia.sdMediaId in keys) { "sd media id missing for ${w.id}" }
            for (p in w.previews.standard) {
                check(p.id in keys) { "preview media id ${p.id} (${w.id}) missing from media map" }
                check(b.media.mediaMap[p.id]!!.keys == MediaEntityKind.WallpaperPreview.requiredKeyStrings) {
                    "preview id ${p.id} keys ${b.media.mediaMap[p.id]!!.keys} != ${MediaEntityKind.WallpaperPreview.requiredKeyStrings}"
                }
            }
        }
        for (a in b.content.artists) {
            check(a.profileImage.id in keys) { "artist ${a.id} profile image missing from media map" }
            check(b.media.mediaMap[a.profileImage.id]!!.keys == MediaEntityKind.ArtistProfile.requiredKeyStrings) {
                "artist ${a.id} profile keys != ${MediaEntityKind.ArtistProfile.requiredKeyStrings}"
            }
        }
        for (f in b.content.folders) {
            check(f.profileImage.id in keys) { "folder ${f.id} profile image missing" }
            check(b.media.mediaMap[f.profileImage.id]!!.keys == MediaEntityKind.FolderProfile.requiredKeyStrings) {
                "folder ${f.id} profile keys != ${MediaEntityKind.FolderProfile.requiredKeyStrings}"
            }
            check(f.featureBannerImage.id in keys) { "folder ${f.id} banner image missing" }
            check(b.media.mediaMap[f.featureBannerImage.id]!!.keys == MediaEntityKind.FolderBanner.requiredKeyStrings) {
                "folder banner ${f.id} keys ${b.media.mediaMap[f.featureBannerImage.id]!!.keys} != ${MediaEntityKind.FolderBanner.requiredKeyStrings}"
            }
        }

        // (a2) every emitted wire key must be one this build knows. Independent of the builder:
        // catches a typo that MediaMapMapper would otherwise silently drop on the client.
        for ((id, map) in b.media.mediaMap) for (k in map.keys) {
            check(SizedImage.fromOrNull(k) != null) { "media id $id carries unknown SizedImage key '$k'" }
        }

        // (a3) the media map must contain exactly the ids the catalog references. Catches orphans
        // (bytes nobody will fetch) and mediaId hash collisions (two seeds, one entry).
        val referenced = buildSet {
            for (w in b.content.wallpapers) {
                add(w.wallpaperDownloadMedia.hdMediaId)
                add(w.wallpaperDownloadMedia.sdMediaId)
                for (p in w.previews.standard) add(p.id)
            }
            for (a in b.content.artists) add(a.profileImage.id)
            for (f in b.content.folders) { add(f.profileImage.id); add(f.featureBannerImage.id) }
        }
        check(keys == referenced) {
            "media map orphan entries ${keys - referenced}; unreferenced catalog ids ${referenced - keys}"
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
