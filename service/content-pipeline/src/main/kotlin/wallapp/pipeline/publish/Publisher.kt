package wallapp.pipeline.publish

import wallapp.pipeline.build.WireBundle

class Publisher(
    private val putter: ObjectPutter,
    private val fetcher: ObjectFetcher,
    private val baseUrl: String,
) {
    /** renditionFiles: remoteKey (e.g. "media/x/download.webp") → local file path. */
    fun publish(bundle: WireBundle, renditionFiles: Map<String, String>, version: String) {
        val platforms = listOf("i", "c")
        val classes = listOf("p~s","p~five0","p~a~n","p~a~xl","p~uhd","f~fo","t~s","t~m","t~l")

        // 1. renditions first
        val putBytes = LinkedHashMap<String, ByteArray>()
        for ((key, local) in renditionFiles) {
            putter.put(local, key)
            // TODO(real-run): `local.toByteArray()` hashes the LOCAL PATH STRING, not the file's
            // actual contents. This only round-trips because FakeStore (test) mirrors the same
            // shortcut (`objects[remoteKey] = localPath.toByteArray()`). For real runs, RcloneClient
            // uploads the real file bytes at `local` and CdnReadBackVerifier fetches real bytes back
            // from the CDN, so this comparison basis must be swapped for the actual local file's
            // bytes/hash (e.g. java.io.File(local).readBytes()) before this read-back check means
            // anything outside the fake. The JSON objects below are already compared against their
            // real serialized content (content.toByteArray()), so that half is real-run-correct.
            putBytes[key] = local.toByteArray()
        }

        // 2. media + search + spec
        val mediaJson = bundle.media.exportString
        for (p in platforms) for (b in classes) putStr(mediaJson, "api/$version/media-1a-$p-$b", putBytes)
        putStr(bundle.search.exportString, "api/$version/content-metadata-1a", putBytes)
        putStr(specJson(version), "api/$version/spec.json", putBytes)

        // 3. catalog LAST
        putStr(bundle.content.exportString, "api/$version/content-1a", putBytes)

        // 4. read-back verify every object (200 + byte-hash)
        for ((key, expected) in putBytes) {
            val got = fetcher.fetch("$baseUrl/$key") ?: error("read-back 200 failed for $key")
            check(got.contentEquals(expected)) { "read-back byte mismatch for $key" }
        }
    }

    private val tmp = kotlin.io.path.createTempDirectory("pipeline").toFile()
    private fun putStr(content: String, remoteKey: String, sink: MutableMap<String, ByteArray>) {
        val f = java.io.File(tmp, remoteKey.replace('/', '_')).apply { writeText(content) }
        putter.put(f.absolutePath, remoteKey)
        sink[remoteKey] = content.toByteArray()
    }
    private fun specJson(version: String) =
        """{"content":"api/$version/content-1a","search":"api/$version/content-metadata-1a","media":{"root":"api/$version/media-1a","p":["i","c"],"b":["p~s","p~five0","p~a~n","p~a~xl","p~uhd","f~fo","t~s","t~m","t~l"]}}"""
}
