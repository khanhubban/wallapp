package wallapp.pipeline.publish

import wallapp.pipeline.build.WireBundle

class Publisher(
    private val putter: ObjectPutter,
    private val fetcher: ObjectFetcher,
    private val baseUrl: String,
) {
    private val platforms = listOf("i", "c")
    private val classes = listOf("p~s","p~five0","p~a~n","p~a~xl","p~uhd","f~fo","t~s","t~m","t~l")

    private companion object {
        /** Extra read-back attempts, each with a distinct cache-busting query. */
        const val CacheBustRetries = 2
    }

    /** renditionFiles: remoteKey (e.g. "media/x/download.webp") → local file path. */
    fun publish(bundle: WireBundle, renditionFiles: Map<String, String>, version: String) {
        // 1. renditions first
        val putBytes = LinkedHashMap<String, ByteArray>()
        for ((key, local) in renditionFiles) {
            putter.put(local, key)
            // Read-back basis = the ACTUAL uploaded bytes: read the real file when it exists (real
            // runs upload the file's contents and the CDN returns them), else fall back to the path
            // string (unit tests use placeholder rendition paths that aren't real files, and the test
            // fakes mirror that fallback so the round-trip still matches). JSON objects below are
            // compared against their real serialized content via putStr — already real-run-correct.
            val f = java.io.File(local)
            putBytes[key] = if (f.isFile) f.readBytes() else local.toByteArray()
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
            verifyReadBack(key, expected)
        }
    }

    /**
     * Shared chrome keys (media/artist/…, media/folder/…) are reused across catalog versions and
     * served `immutable`, so a PUT does not invalidate the edge. A read-back straight after the
     * upload can therefore be answered from cache with the previous object, and a transient edge
     * error can answer with nothing at all. Neither means the upload was corrupt.
     *
     * Retry past the cache with a busting query before failing. Only a response that still differs
     * once origin has been consulted is a real mismatch.
     */
    private fun verifyReadBack(key: String, expected: ByteArray) {
        val url = "$baseUrl/$key"
        var sawResponse = false
        for (attempt in 0..CacheBustRetries) {
            val target = if (attempt == 0) url else "$url?cacheBust=$attempt"
            val got = fetcher.fetch(target) ?: continue
            sawResponse = true
            if (got.contentEquals(expected)) return
        }
        check(sawResponse) { "read-back 200 failed for $key" }
        error("read-back byte mismatch for $key")
    }

    private val tmp = kotlin.io.path.createTempDirectory("pipeline").toFile()
    private fun putStr(content: String, remoteKey: String, sink: MutableMap<String, ByteArray>) {
        val f = java.io.File(tmp, remoteKey.replace('/', '_')).apply { writeText(content) }
        putter.put(f.absolutePath, remoteKey)
        sink[remoteKey] = content.toByteArray()
    }
    private fun specJson(version: String): String {
        val p = platforms.joinToString(",") { "\"$it\"" }
        val b = classes.joinToString(",") { "\"$it\"" }
        return """{"content":"api/$version/content-1a","search":"api/$version/content-metadata-1a","media":{"root":"api/$version/media-1a","p":[$p],"b":[$b]}}"""
    }
}
