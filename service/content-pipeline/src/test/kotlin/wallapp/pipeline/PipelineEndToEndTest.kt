package wallapp.pipeline

import wallapp.pipeline.publish.*
import kotlin.test.Test
import kotlin.test.assertTrue

class PipelineEndToEndTest {
    @Test fun buildValidatePublishHappyPath() {
        val manifest = """
        {"version":"20260708-01","baseUrl":"https://media-staging.stillscenes.app",
         "artist":{"id":"stillscenes","label":"StillScenes","profileImagePath":"media/a/p.webp"},
         "folder":{"id":"f~justadded","title":"Just Added","profileImagePath":"media/f/p.webp","featureBannerImagePath":"media/f/b.webp"},
         "wallpapers":[{"id":"stillscenes_1a2b3c4d","label":"Aurora 01","isDark":true,"width":1440,"height":3120,
           "downloadRenditionPath":"media/x/download.webp","previewRenditionPath":"media/x/preview.webp","tags":["dark"]}]}
        """.trimIndent()
        // Fake mirrors a real ObjectPutter/ObjectFetcher: Publisher writes each JSON wire file
        // (media/search/spec/catalog) to a REAL temp file and puts that file's path, recording the
        // JSON *content* bytes as "expected" for read-back. If put() naively hashed the path string
        // (the brief's literal fake), the JSON read-back would MISMATCH content vs. path-string bytes
        // and this happy-path test would throw instead of passing (the same bug Task 8 hit in
        // PublisherTest's FakeStore). Fix: when `localPath` names a real file on disk, store its
        // actual bytes; otherwise fall back to hashing the path string. The rendition entries below
        // use placeholder paths (identical to their remote keys) that are never created on disk here,
        // so those deterministically fall back to the path-string branch.
        val store = object : ObjectPutter, ObjectFetcher {
            val objects = HashMap<String, ByteArray>()
            override fun put(localPath: String, remoteKey: String) {
                val f = java.io.File(localPath)
                objects[remoteKey] = if (f.isFile) f.readBytes() else localPath.toByteArray()
            }
            override fun fetch(url: String) = objects[url.substringAfter(".app/")]
        }
        // rendition local paths keyed by remote object key (must equal manifest paths)
        val renditions = mapOf("media/x/download.webp" to "media/x/download.webp","media/x/preview.webp" to "media/x/preview.webp",
            "media/a/p.webp" to "media/a/p.webp","media/f/p.webp" to "media/f/p.webp","media/f/b.webp" to "media/f/b.webp")
        runPipeline(manifest, store, store, renditions)
        assertTrue(store.objects.keys.any { it.endsWith("content-1a") })
    }
}
