package wallapp.pipeline.publish

import wallapp.pipeline.IMGIX_HOST_PREFIX
import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PublisherTest {
    private fun bundle(): WireBundle {
        val m = PipelineManifest("20260708-01", "https://media-staging.stillscenes.app",
            ManifestArtist("stillscenes", "StillScenes", "media/a/p.webp"),
            ManifestFolder("f~justadded", "Just Added", "media/f/p.webp", "media/f/b.webp"),
            listOf(ManifestWallpaper("stillscenes_1a2b3c4d","Aurora 01",true,1440,3120,
                "media/x/download.webp","media/x/preview.webp", tags = listOf("dark"))))
        return WireBundle(CatalogBuilder.build(m), SearchBuilder.build(m), MediaMapBuilder.build(m),
            m.baseUrl, IMGIX_HOST_PREFIX)
    }

    // Fake stores puts; fetcher returns exactly what was put (byte-identical) → read-back passes.
    // put() mirrors a real ObjectPutter: if `localPath` names a real file (true for every JSON
    // object Publisher writes via putStr), store its actual bytes. The renditions in these tests
    // use placeholder paths ("/tmp/download.webp" etc.) that are never created on disk, so for
    // those it falls back to encoding the path string itself — matching Publisher's rendition
    // bookkeeping, which (per the TODO in Publisher.kt) also just hashes the path string today.
    private class FakeStore : ObjectPutter, ObjectFetcher {
        val objects = LinkedHashMap<String, ByteArray>()
        val putOrder = mutableListOf<String>()
        override fun put(localPath: String, remoteKey: String) {
            putOrder += remoteKey
            val f = java.io.File(localPath)
            objects[remoteKey] = if (f.isFile) f.readBytes() else localPath.toByteArray()
        }
        override fun fetch(url: String): ByteArray? = objects[url.substringAfter(".app/")]
    }

    @Test fun publishesCatalogLastAndReadBackPasses() {
        val store = FakeStore()
        // rendition "files" are keyed by remote object key → local path
        val renditions = mapOf("media/x/download.webp" to "/tmp/download.webp", "media/x/preview.webp" to "/tmp/preview.webp",
            "media/a/p.webp" to "/tmp/ap.webp", "media/f/p.webp" to "/tmp/fp.webp", "media/f/b.webp" to "/tmp/fb.webp")
        Publisher(store, store, "https://media-staging.stillscenes.app").publish(bundle(), renditions, "20260708-01")
        assertTrue(store.putOrder.last().endsWith("content-1a")) // catalog last
        assertTrue(store.putOrder.any { it.endsWith("media-1a-c-p~s") })
    }

    @Test fun abortsWhenReadBackBytesDiffer() {
        val store = FakeStore()
        val corrupt = object : ObjectPutter by store, ObjectFetcher { override fun fetch(url: String) = "TAMPERED".toByteArray() }
        val renditions = mapOf("media/x/download.webp" to "/tmp/d", "media/x/preview.webp" to "/tmp/p",
            "media/a/p.webp" to "/tmp/ap", "media/f/p.webp" to "/tmp/fp", "media/f/b.webp" to "/tmp/fb")
        assertFailsWith<IllegalStateException> {
            Publisher(store, corrupt, "https://media-staging.stillscenes.app").publish(bundle(), renditions, "20260708-01")
        }
    }

    /**
     * Shared chrome keys (media/artist/..., media/folder/...) are reused across catalog versions and
     * served `immutable`, so the edge can answer a read-back with a stale copy of an object we just
     * overwrote. Retry past the cache before declaring the upload corrupt.
     */
    @Test fun staleEdgeCopyIsRetriedPastTheCacheRatherThanFailing() {
        val store = FakeStore()
        val edge = object : ObjectPutter by store, ObjectFetcher {
            val urls = mutableListOf<String>()
            override fun fetch(url: String): ByteArray? {
                urls += url
                // The cached edge answers the bare URL with yesterday's bytes; a cache-busted
                // request revalidates against origin, which holds what we just put.
                return if ("?" in url) store.fetch(url.substringBefore("?")) else "STALE".toByteArray()
            }
        }
        val renditions = mapOf("media/a/p.webp" to "/tmp/ap")

        Publisher(store, edge, "https://media-staging.stillscenes.app")
            .publish(bundle(), renditions, "20260708-01") // no throw

        assertTrue(edge.urls.any { "?" in it }, "expected a cache-busted retry")
    }

    @Test fun originThatGenuinelyDiffersStillAborts() {
        val store = FakeStore()
        // Every request, cache-busted or not, returns the wrong bytes: a real corruption.
        val corrupt = object : ObjectPutter by store, ObjectFetcher {
            override fun fetch(url: String) = "TAMPERED".toByteArray()
        }
        val renditions = mapOf("media/a/p.webp" to "/tmp/ap")

        val e = assertFailsWith<IllegalStateException> {
            Publisher(store, corrupt, "https://media-staging.stillscenes.app")
                .publish(bundle(), renditions, "20260708-01")
        }
        assertTrue(e.message!!.contains("byte mismatch"), "got: ${e.message}")
    }

    @Test fun aTransientlyUnreachableObjectIsRetriedBeforeFailing() {
        val store = FakeStore()
        val flaky = object : ObjectPutter by store, ObjectFetcher {
            var calls = 0
            override fun fetch(url: String): ByteArray? {
                calls++
                return if (calls == 1) null else store.fetch(url.substringBefore("?"))
            }
        }
        val renditions = mapOf("media/a/p.webp" to "/tmp/ap")

        Publisher(store, flaky, "https://media-staging.stillscenes.app")
            .publish(bundle(), renditions, "20260708-01") // no throw
    }

    // Real-run branch: when a rendition path names an actual file, the read-back basis must be the
    // FILE's bytes, not the path string. Content here differs from the path, so the OLD code (which
    // recorded local.toByteArray()) would mismatch the fake's real-file bytes and throw.
    @Test fun readBackUsesRealRenditionFileBytesNotPathString() {
        val store = FakeStore()
        val tmp = kotlin.io.path.createTempDirectory("pubtest").toFile()
        val real = java.io.File(tmp, "download.webp").apply { writeBytes(byteArrayOf(0x52, 0x49, 0x46, 0x46, 1, 2, 3, 4)) }
        val renditions = mapOf(
            "media/x/download.webp" to real.absolutePath, "media/x/preview.webp" to real.absolutePath,
            "media/a/p.webp" to real.absolutePath, "media/f/p.webp" to real.absolutePath, "media/f/b.webp" to real.absolutePath,
        )
        Publisher(store, store, "https://media-staging.stillscenes.app").publish(bundle(), renditions, "20260708-01") // no throw
        val stored = store.objects["media/x/download.webp"]!!
        assertTrue(stored.contentEquals(real.readBytes()))                    // file bytes...
        assertTrue(!stored.contentEquals(real.absolutePath.toByteArray()))    // ...not the path string
    }
}
