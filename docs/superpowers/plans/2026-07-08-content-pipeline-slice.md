# Content Pipeline — Thin Vertical Slice Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Generate a small real set of AI wallpapers and publish them in the existing demo wire format so the untouched client renders them, with a publish that cannot ship a catalog the client can't decode.

**Architecture:** A **Python script** owns all pixel work (FLUX generation → Real-ESRGAN upscale → per-wallpaper renditions → EXIF strip) and emits renditions + a `manifest.json` handoff. A new **`kotlin("jvm")` `service:content-pipeline` module** consumes the manifest and owns model/JSON/publish work: BUILD (construct `NetworkContent`/`NetworkSearchMetadata`/media maps with the app's own serializers) → VALIDATE (strict decode + referential integrity + invariants) → PUBLISH (rclone PUT to R2 staging + CDN read-back). *(Refinement from spec §5, which placed RENDER in Kotlin: image manipulation moves to Python — Pillow has native WebP, the JVM does not, and the script already does pixel work. Behavior and scope are unchanged.)*

**Tech Stack:** Kotlin/JVM (Gradle, kotlinx.serialization — reusing `content-network`/`mediamap-network`/`search-model`); Python 3 (Pillow, requests); `realesrgan-ncnn-vulkan`; `rclone`; Ktor client for CDN read-back.

## Global Constraints

- **Wire format = the demo format**, byte-shape-compatible with `demo-assets/api/99999999/*`. Files: `content-1a`, `content-metadata-1a`, `media-1a-<p>-<b>` (18: `p∈{i,c}` × `b∈{p~s,p~five0,p~a~n,p~a~xl,p~uhd,f~fo,t~s,t~m,t~l}`), `spec.json`. **No `key` file.**
- **Serialize only via the app's own models** — `NetworkContent.exportString`, `NetworkSearchMetadata.exportString`, `NetworkMediaData.exportString`. Never hand-roll JSON.
- **Media maps are keyed by numeric media ID (`Long`)**, value `NetworkMediaMap = Map<String,String>` (`SizedImage.key → absolute url`). NOT keyed by remixId.
- **`NetworkMediaData.fromJson` swallows errors → `Empty`** — VALIDATE must decode media **strictly** (`Json { ignoreUnknownKeys = true }.decodeFromString<NetworkMediaData>()` in a try that FAILS the run), never via `fromJson`.
- **Rendition URLs MUST NOT start with the imgix host prefix** (`https://<appname>.imgix.net`) — the client only rewrites imgix URLs; ours must pass through unchanged.
- **`<version>` is write-once** — never re-PUT an existing version path; bump `-NN` on any re-run. Version string is opaque to the client (interpolated into the URL); use `YYYYMMDD-NN`.
- **Publish is strict-order**: renditions → media/search files → `content-1a` last. Read-back mismatch aborts and does **not** flip RC.
- Base URL: `https://media-staging.stillscenes.app`. Buckets: content → R2 staging; masters → `stillscenes-masters`. Depends on the Cloudflare custom-domain gate (foundation Task 10) for the live read-back only.
- Tests run under the repo's existing `desktopTest`/JUnit convention; commit after every green step.

## File Structure

- `settings.gradle.kts` — add `":service:content-pipeline"` to the service `include(...)`.
- `service/content-pipeline/content-pipeline.gradle.kts` — new `kotlin("jvm")` module; deps: `content-network`, `mediamap-network`, `search-model`, `core:common`, ktor client, `libs.kotlinx.coroutines.core`.
- `service/content-pipeline/src/main/kotlin/wallapp/pipeline/`
  - `manifest/PipelineManifest.kt` — the Python→Kotlin handoff data classes (+ parser).
  - `build/MediaIds.kt` — deterministic `Long` media-id + rendition-URL helpers.
  - `build/MediaMapBuilder.kt` — `manifest → NetworkMediaData`.
  - `build/CatalogBuilder.kt` — `manifest → NetworkContent`.
  - `build/SearchBuilder.kt` — `manifest → NetworkSearchMetadata`.
  - `build/WireBundle.kt` — the built artifact set (catalog+search+media+spec strings).
  - `validate/CatalogValidator.kt` — strict decode + referential + invariant gates.
  - `publish/RcloneClient.kt` — `ProcessBuilder` wrapper for rclone PUT.
  - `publish/CdnReadBackVerifier.kt` — Ktor GET + hash + strict decode.
  - `publish/Publisher.kt` — strict-order publish orchestration.
  - `Main.kt` — `publish` entrypoint (reads manifest dir, runs build→validate→publish).
- `service/content-pipeline/src/test/kotlin/wallapp/pipeline/` — one test file per unit above.
- `script/content_pipeline/generate.py` — FLUX generate → upscale → render renditions → emit `manifest.json`.
- `script/content_pipeline/README.md` — env vars, rclone remote, run instructions.

---

### Task 1: Scaffold the `service:content-pipeline` JVM module

**Files:**
- Modify: `settings.gradle.kts` (the `include(":service:service-common", …)` block)
- Create: `service/content-pipeline/content-pipeline.gradle.kts`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/ModuleSmokeTest.kt`

**Interfaces:**
- Produces: a compiling `:service:content-pipeline` module that can `import` `wallapp.content.network.model.NetworkContent`.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline

import wallapp.content.network.model.NetworkContent
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleSmokeTest {
    @Test fun canConstructAndRoundTripEmptyCatalog() {
        val c = NetworkContent(wallpapers = emptyList(), categories = emptyList(), artists = emptyList(), folders = emptyList())
        assertEquals(c, NetworkContent.fromExportString(c.exportString))
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test`
Expected: FAIL — project `:service:content-pipeline` not found / unresolved.

- [ ] **Step 3: Create the module gradle file**

`service/content-pipeline/content-pipeline.gradle.kts`:
```kotlin
plugins {
    kotlin("jvm")
}

group = "com.wallapp.service.contentpipeline"
version = "1.0.0"

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    implementation(project(":shared:core:common"))
    implementation(project(":shared:data:content-network"))
    implementation(project(":shared:data:mediamap-network"))
    implementation(project(":shared:data:search-model"))
    testImplementation(kotlin("test"))
}

tasks.test { useJUnitPlatform() }
```

Add to `settings.gradle.kts` inside the existing service `include(...)`:
```kotlin
    ":service:content-pipeline",
```
(Verify `libs.ktor.client.cio` exists in the version catalog; if the alias differs, use the repo's CIO alias. If `useJUnitPlatform()` clashes with the repo default, match `service:service-common`'s test config.)

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add settings.gradle.kts service/content-pipeline
git commit -m "feat(pipeline): scaffold service:content-pipeline JVM module"
```

---

### Task 2: The `manifest.json` handoff contract

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/manifest/PipelineManifest.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/manifest/PipelineManifestTest.kt`

**Interfaces:**
- Produces: `PipelineManifest.parse(json: String): PipelineManifest`; nested `ManifestWallpaper`, `ManifestImage`, `ManifestArtist`, `ManifestFolder`.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.manifest

import kotlin.test.Test
import kotlin.test.assertEquals

class PipelineManifestTest {
    @Test fun parsesMinimalManifest() {
        val json = """
        {"version":"20260708-01","baseUrl":"https://media-staging.stillscenes.app",
         "artist":{"id":"stillscenes","label":"StillScenes","profileImagePath":"media/artist/stillscenes/profile.webp"},
         "folder":{"id":"f~justadded","title":"Just Added","profileImagePath":"media/folder/justadded/profile.webp","featureBannerImagePath":"media/folder/justadded/banner.webp"},
         "wallpapers":[{"id":"stillscenes_1a2b3c4d","label":"Aurora 01","isDark":true,"width":1440,"height":3120,
           "downloadRenditionPath":"media/stillscenes_1a2b3c4d/download.webp","previewRenditionPath":"media/stillscenes_1a2b3c4d/preview.webp",
           "styles":["amoled"],"tags":["dark","minimal"],"colors":["dark"]}]}
        """.trimIndent()
        val m = PipelineManifest.parse(json)
        assertEquals("20260708-01", m.version)
        assertEquals(1, m.wallpapers.size)
        assertEquals("stillscenes_1a2b3c4d", m.wallpapers[0].id)
        assertEquals("media/stillscenes_1a2b3c4d/download.webp", m.wallpapers[0].downloadRenditionPath)
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*PipelineManifestTest*'`
Expected: FAIL — unresolved reference `PipelineManifest`.

- [ ] **Step 3: Implement**

```kotlin
package wallapp.pipeline.manifest

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PipelineManifest(
    val version: String,
    val baseUrl: String,
    val artist: ManifestArtist,
    val folder: ManifestFolder,
    val wallpapers: List<ManifestWallpaper>,
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun parse(text: String): PipelineManifest = json.decodeFromString(text)
    }
}

@Serializable
data class ManifestArtist(val id: String, val label: String, val profileImagePath: String)

@Serializable
data class ManifestFolder(
    val id: String, val title: String,
    val profileImagePath: String, val featureBannerImagePath: String,
)

@Serializable
data class ManifestWallpaper(
    val id: String,
    val label: String,
    val isDark: Boolean,
    val width: Int,
    val height: Int,
    val downloadRenditionPath: String,
    val previewRenditionPath: String,
    val styles: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
)
```

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*PipelineManifestTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): define manifest.json handoff contract"
```

---

### Task 3: Deterministic media-ID + rendition-URL helpers

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/MediaIds.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/MediaIdsTest.kt`

**Interfaces:**
- Produces: `mediaId(seed: String): Long` (stable, positive); `renditionUrl(baseUrl: String, path: String): String`.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.build

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaIdsTest {
    @Test fun mediaIdIsStableAndPositive() {
        val a = mediaId("stillscenes_1a2b3c4d:download")
        assertEquals(a, mediaId("stillscenes_1a2b3c4d:download")) // stable
        assertTrue(a > 0L)
        assertTrue(a != mediaId("stillscenes_1a2b3c4d:preview")) // distinct roles differ
    }
    @Test fun renditionUrlJoinsWithoutDoubleSlash() {
        assertEquals(
            "https://media-staging.stillscenes.app/media/x/download.webp",
            renditionUrl("https://media-staging.stillscenes.app", "media/x/download.webp"),
        )
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*MediaIdsTest*'`
Expected: FAIL — unresolved `mediaId`.

- [ ] **Step 3: Implement**

```kotlin
package wallapp.pipeline.build

import java.security.MessageDigest

/** Stable positive Long id derived from a seed (e.g. "<remixId>:download"). */
fun mediaId(seed: String): Long {
    val d = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray())
    var v = 0L
    for (i in 0 until 7) v = (v shl 8) or (d[i].toLong() and 0xFF) // 56 bits → always positive
    return v
}

fun renditionUrl(baseUrl: String, path: String): String =
    baseUrl.trimEnd('/') + "/" + path.trimStart('/')
```

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*MediaIdsTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): deterministic media-id and rendition-url helpers"
```

---

### Task 4: MediaMapBuilder — `manifest → NetworkMediaData`

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/MediaMapBuilder.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/MediaMapBuilderTest.kt`

**Interfaces:**
- Consumes: `PipelineManifest`, `mediaId`, `renditionUrl`.
- Produces: `MediaMapBuilder.build(m: PipelineManifest): NetworkMediaData`. Media-id roles: `<id>:download` → `{dhd,dsd}`; `<id>:preview` → `{s, wfs, wft, fs}`; `<artistId>:profile` → `{am, as, e}`; `<folderId>:profile`/`:banner` → `{wfs}`. (Minimal correct inner-key sets; per-class differentiation deferred.)

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.build

import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MediaMapBuilderTest {
    private fun manifest() = PipelineManifest(
        version = "20260708-01", baseUrl = "https://media-staging.stillscenes.app",
        artist = ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        folder = ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        wallpapers = listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp")),
    )

    @Test fun downloadIdCarriesDhdDsd() {
        val data = MediaMapBuilder.build(manifest())
        val dl = data.mediaMap[mediaId("stillscenes_1a2b3c4d:download")]!!
        assertEquals(setOf("dhd", "dsd"), dl.keys)
        assertTrue(dl["dhd"]!!.endsWith("/media/stillscenes_1a2b3c4d/download.webp"))
    }

    @Test fun previewIdCarriesFeedAndFullscreenKeys() {
        val data = MediaMapBuilder.build(manifest())
        val pv = data.mediaMap[mediaId("stillscenes_1a2b3c4d:preview")]!!
        assertEquals(setOf("s", "wfs", "wft", "fs"), pv.keys)
    }

    @Test fun artistProfileIdCarriesArtistKeys() {
        val data = MediaMapBuilder.build(manifest())
        val ar = data.mediaMap[mediaId("stillscenes:profile")]!!
        assertEquals(setOf("am", "as", "e"), ar.keys)
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*MediaMapBuilderTest*'`
Expected: FAIL — unresolved `MediaMapBuilder`.

- [ ] **Step 3: Implement**

```kotlin
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
```

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*MediaMapBuilderTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): build media maps keyed by numeric media id"
```

---

### Task 5: CatalogBuilder — `manifest → NetworkContent`

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/CatalogBuilder.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/CatalogBuilderTest.kt`

**Interfaces:**
- Consumes: `PipelineManifest`, `mediaId`.
- Produces: `CatalogBuilder.build(m: PipelineManifest): NetworkContent`. One synthetic artist, one category (`<artist>~singles`), one folder; every wallpaper is `type="parallax"`, `isSingle=true`, `categoryId` = the category id.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.build

import wallapp.content.network.model.NetworkContent
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CatalogBuilderTest {
    private fun manifest() = PipelineManifest(
        "20260708-01", "https://media-staging.stillscenes.app",
        ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp")),
    )

    @Test fun catalogHasAllRequiredArraysAndResolvesReferences() {
        val c: NetworkContent = CatalogBuilder.build(manifest())
        assertEquals(1, c.wallpapers.size); assertEquals(1, c.categories.size)
        assertEquals(1, c.artists.size); assertEquals(1, c.folders.size)
        val w = c.wallpapers[0]
        assertEquals("stillscenes", w.artistId)
        assertEquals(c.categories[0].id, w.categoryId)          // categoryId resolves
        assertEquals(mediaId("stillscenes_1a2b3c4d:download"), w.wallpaperDownloadMedia.hdMediaId)
        assertEquals(mediaId("stillscenes_1a2b3c4d:preview"), w.previews.standard[0].id)
        assertTrue(c.categories[0].remixIds.contains(w.id))      // category lists the remix
    }

    @Test fun roundTripsThroughClientModel() {
        val c = CatalogBuilder.build(manifest())
        assertEquals(c, NetworkContent.fromExportString(c.exportString))
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*CatalogBuilderTest*'`
Expected: FAIL — unresolved `CatalogBuilder`.

- [ ] **Step 3: Implement**

```kotlin
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
```

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*CatalogBuilderTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): build NetworkContent catalog from manifest"
```

---

### Task 6: SearchBuilder — `manifest → NetworkSearchMetadata`

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/SearchBuilder.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/SearchBuilderTest.kt`

**Interfaces:**
- Consumes: `PipelineManifest`.
- Produces: `SearchBuilder.build(m: PipelineManifest): NetworkSearchMetadata`. One remix-metadata per wallpaper (with non-empty `titleSuggestions`), one artist-metadata, one folder-metadata.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.build

import wallapp.search.model.NetworkSearchMetadata
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchBuilderTest {
    private fun manifest() = PipelineManifest(
        "20260708-01", "https://media-staging.stillscenes.app",
        ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp",
            styles = listOf("amoled"), tags = listOf("dark", "minimal"), colors = listOf("dark"))),
    )

    @Test fun buildsRequiredArraysWithTitleSuggestions() {
        val s: NetworkSearchMetadata = SearchBuilder.build(manifest())
        assertEquals(1, s.remixMetadata.size)
        assertEquals(1, s.artistMetadata.size)
        assertEquals(1, s.folderMetadata.size)
        val r = s.remixMetadata[0]
        assertEquals("stillscenes_1a2b3c4d", r.remixId)
        assertTrue(r.titleSuggestions.isNotEmpty())   // required, non-empty
        assertTrue(r.tags.any { it.term == "dark" })
    }

    @Test fun roundTripsThroughClientModel() {
        val s = SearchBuilder.build(manifest())
        assertEquals(s, NetworkSearchMetadata.fromExportString(s.exportString))
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*SearchBuilderTest*'`
Expected: FAIL — unresolved `SearchBuilder`.

- [ ] **Step 3: Implement**

```kotlin
package wallapp.pipeline.build

import wallapp.search.model.*
import wallapp.pipeline.manifest.PipelineManifest

object SearchBuilder {
    private fun entries(terms: List<String>): List<NetworkSearchEntry> =
        terms.mapIndexed { i, t -> NetworkSearchEntry(term = t, relevance = (1.0f - i * 0.05f).coerceAtLeast(0.5f)) }

    fun build(m: PipelineManifest): NetworkSearchMetadata {
        val remix = m.wallpapers.map { w ->
            NetworkSearchRemixMetadata(
                remixId = w.id,
                artistNames = listOf(m.artist.label),
                title = w.label,
                styles = entries(w.styles),
                tags = entries(w.tags),
                colors = entries(w.colors),
                searchTerms = entries((w.tags + w.styles).distinct()),
                titleSuggestions = entries(listOf(w.label)),
            )
        }
        val artist = listOf(NetworkSearchArtistMetadata(m.artist.id, entries(listOf(m.artist.label))))
        val folder = listOf(NetworkSearchFolderMetadata(m.folder.id, entries(listOf(m.folder.title))))
        return NetworkSearchMetadata(remix, artist, folder)
    }
}
```

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*SearchBuilderTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): build NetworkSearchMetadata from manifest"
```

---

### Task 7: CatalogValidator — strict decode + referential + invariants

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/WireBundle.kt`
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/validate/CatalogValidator.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/validate/CatalogValidatorTest.kt`

**Interfaces:**
- Consumes: the three built models (Tasks 4–6).
- Produces: `WireBundle` (holds `content: NetworkContent`, `search: NetworkSearchMetadata`, `media: NetworkMediaData`, `baseUrl: String`, `imgixHostPrefix: String`). `CatalogValidator.validate(b: WireBundle)` — throws `IllegalStateException` on any failure; returns normally on success.
- Gates: (a) every `Long` media id referenced by the catalog is a **key** in the media map; (b) required inner `SizedImage.key`s present per role (download→`dhd,dsd`; preview→`s,wfs`); (c) `content` wallpaper-id set == `search` remixId set; (d) no media URL starts with the imgix prefix; (e) strict re-decode of every wire file (media via strict `Json`, NOT `fromJson`).

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.validate

import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class CatalogValidatorTest {
    private fun manifest() = PipelineManifest(
        "20260708-01", "https://media-staging.stillscenes.app",
        ManifestArtist("stillscenes", "StillScenes", "media/artist/stillscenes/profile.webp"),
        ManifestFolder("f~justadded", "Just Added", "media/folder/justadded/profile.webp", "media/folder/justadded/banner.webp"),
        listOf(ManifestWallpaper("stillscenes_1a2b3c4d", "Aurora 01", true, 1440, 3120,
            "media/stillscenes_1a2b3c4d/download.webp", "media/stillscenes_1a2b3c4d/preview.webp",
            tags = listOf("dark"))),
    )
    private fun bundle(base: String = "https://media-staging.stillscenes.app") = WireBundle(
        content = CatalogBuilder.build(manifest().copy(baseUrl = base)),
        search = SearchBuilder.build(manifest()),
        media = MediaMapBuilder.build(manifest().copy(baseUrl = base)),
        baseUrl = base,
        imgixHostPrefix = "https://stillscenes.imgix.net",
    )

    @Test fun passesForACoherentBundle() {
        CatalogValidator.validate(bundle()) // no throw
    }

    @Test fun failsWhenAMediaIdIsMissingFromTheMap() {
        val b = bundle()
        val broken = b.copy(media = wallapp.media.network.model.NetworkMediaData(1, emptyMap()))
        assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
    }

    @Test fun failsWhenRenditionUrlIsOnImgixHost() {
        assertFailsWith<IllegalStateException> {
            CatalogValidator.validate(bundle(base = "https://stillscenes.imgix.net"))
        }
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*CatalogValidatorTest*'`
Expected: FAIL — unresolved `WireBundle`/`CatalogValidator`.

- [ ] **Step 3: Implement**

`WireBundle.kt`:
```kotlin
package wallapp.pipeline.build

import wallapp.content.network.model.NetworkContent
import wallapp.media.network.model.NetworkMediaData
import wallapp.search.model.NetworkSearchMetadata

data class WireBundle(
    val content: NetworkContent,
    val search: NetworkSearchMetadata,
    val media: NetworkMediaData,
    val baseUrl: String,
    val imgixHostPrefix: String,
)
```

`CatalogValidator.kt`:
```kotlin
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
```

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*CatalogValidatorTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): validate referential integrity + invariants"
```

---

### Task 8: Publish — rclone PUT + CDN read-back (with fakes)

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/publish/RcloneClient.kt`
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/publish/CdnReadBackVerifier.kt`
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/publish/Publisher.kt`
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/publish/PublisherTest.kt`

**Interfaces:**
- `interface ObjectPutter { fun put(localPath: String, remoteKey: String) }` (real impl = `RcloneClient`).
- `interface ObjectFetcher { fun fetch(url: String): ByteArray? }` (real impl = `CdnReadBackVerifier` over Ktor).
- `Publisher(putter, fetcher, baseUrl).publish(bundle, renditionFiles: Map<String,String>, version): Unit` — strict order, read-back-verifies, throws on mismatch before returning (caller flips RC only on success).

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.pipeline.publish

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
            m.baseUrl, "https://stillscenes.imgix.net")
    }

    // Fake stores puts; fetcher returns exactly what was put (byte-identical) → read-back passes.
    private class FakeStore : ObjectPutter, ObjectFetcher {
        val objects = LinkedHashMap<String, ByteArray>()
        val putOrder = mutableListOf<String>()
        override fun put(localPath: String, remoteKey: String) { putOrder += remoteKey; objects[remoteKey] = localPath.toByteArray() }
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
        val store = object : FakeStore() {}
        val corrupt = object : ObjectPutter by store, ObjectFetcher { override fun fetch(url: String) = "TAMPERED".toByteArray() }
        val renditions = mapOf("media/x/download.webp" to "/tmp/d", "media/x/preview.webp" to "/tmp/p",
            "media/a/p.webp" to "/tmp/ap", "media/f/p.webp" to "/tmp/fp", "media/f/b.webp" to "/tmp/fb")
        assertFailsWith<IllegalStateException> {
            Publisher(store, corrupt, "https://media-staging.stillscenes.app").publish(bundle(), renditions, "20260708-01")
        }
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*PublisherTest*'`
Expected: FAIL — unresolved `Publisher`/`ObjectPutter`.

- [ ] **Step 3: Implement**

`RcloneClient.kt` (real putter; not exercised by unit tests):
```kotlin
package wallapp.pipeline.publish

interface ObjectPutter { fun put(localPath: String, remoteKey: String) }
interface ObjectFetcher { fun fetch(url: String): ByteArray? }

/** PUTs one object to R2 via `rclone copyto <local> <remote>:<bucket>/<key>`. */
class RcloneClient(private val remote: String, private val bucket: String) : ObjectPutter {
    override fun put(localPath: String, remoteKey: String) {
        val p = ProcessBuilder("rclone", "copyto", localPath, "$remote:$bucket/$remoteKey")
            .redirectErrorStream(true).start()
        val out = p.inputStream.bufferedReader().readText()
        check(p.waitFor() == 0) { "rclone put failed for $remoteKey: $out" }
    }
}
```

`CdnReadBackVerifier.kt`:
```kotlin
package wallapp.pipeline.publish

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.runBlocking

class CdnReadBackVerifier : ObjectFetcher {
    private val client = HttpClient(CIO)
    override fun fetch(url: String): ByteArray? = runBlocking {
        val resp = client.get(url)
        if (resp.status.isSuccess()) resp.readBytes() else null
    }
}
```

`Publisher.kt`:
```kotlin
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
        for ((key, local) in renditionFiles) { putter.put(local, key); putBytes[key] = local.toByteArray() }

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
```

> Note for the implementer: the fake in the test stores `localPath.toByteArray()` as the object bytes and the Publisher mirrors that for rendition read-back, so the fake round-trips. For real runs, `RcloneClient` uploads the actual file and `CdnReadBackVerifier` fetches real bytes; align the read-back comparison to hash the real local file (swap `local.toByteArray()` for the file's bytes/hash). Keep the JSON-object path exact.

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*PublisherTest*'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src
git commit -m "feat(pipeline): strict-order publish with CDN read-back verification"
```

---

### Task 9: `publish` entrypoint wiring

**Files:**
- Create: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/Main.kt`
- Modify: `service/content-pipeline/content-pipeline.gradle.kts` (add `application`/run task)
- Test: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/PipelineEndToEndTest.kt`

**Interfaces:**
- Consumes: all builders + validator + publisher.
- Produces: `runPipeline(manifestText: String, putter, fetcher): Unit` — build → validate → publish. `main()` reads a manifest file path from args and wires `RcloneClient`/`CdnReadBackVerifier`.

- [ ] **Step 1: Write the failing test**

```kotlin
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
        val store = object : ObjectPutter, ObjectFetcher {
            val objects = HashMap<String, ByteArray>()
            override fun put(localPath: String, remoteKey: String) { objects[remoteKey] = localPath.toByteArray() }
            override fun fetch(url: String) = objects[url.substringAfter(".app/")]
        }
        // rendition local paths keyed by remote object key (must equal manifest paths)
        val renditions = mapOf("media/x/download.webp" to "media/x/download.webp","media/x/preview.webp" to "media/x/preview.webp",
            "media/a/p.webp" to "media/a/p.webp","media/f/p.webp" to "media/f/p.webp","media/f/b.webp" to "media/f/b.webp")
        runPipeline(manifest, store, store, renditions)
        assertTrue(store.objects.keys.any { it.endsWith("content-1a") })
    }
}
```

- [ ] **Step 2: Run to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*PipelineEndToEndTest*'`
Expected: FAIL — unresolved `runPipeline`.

- [ ] **Step 3: Implement**

```kotlin
package wallapp.pipeline

import wallapp.pipeline.build.*
import wallapp.pipeline.manifest.PipelineManifest
import wallapp.pipeline.publish.*
import wallapp.pipeline.validate.CatalogValidator

private const val IMGIX_PREFIX = "https://stillscenes.imgix.net"

fun runPipeline(manifestText: String, putter: ObjectPutter, fetcher: ObjectFetcher, renditionFiles: Map<String, String>) {
    val m = PipelineManifest.parse(manifestText)
    val bundle = WireBundle(
        content = CatalogBuilder.build(m),
        search = SearchBuilder.build(m),
        media = MediaMapBuilder.build(m),
        baseUrl = m.baseUrl,
        imgixHostPrefix = IMGIX_PREFIX,
    )
    CatalogValidator.validate(bundle)                       // fail-fast BEFORE any upload
    Publisher(putter, fetcher, m.baseUrl).publish(bundle, renditionFiles, m.version)
}

fun main(args: Array<String>) {
    val manifestPath = args.firstOrNull() ?: error("usage: publish <manifest.json>")
    val text = java.io.File(manifestPath).readText()
    val m = PipelineManifest.parse(text)
    val dir = java.io.File(manifestPath).parentFile
    // remote key == manifest relative path; local file lives under the manifest dir
    val renditions = buildMap {
        m.wallpapers.forEach { put(it.downloadRenditionPath, java.io.File(dir, it.downloadRenditionPath).absolutePath)
                               put(it.previewRenditionPath, java.io.File(dir, it.previewRenditionPath).absolutePath) }
        put(m.artist.profileImagePath, java.io.File(dir, m.artist.profileImagePath).absolutePath)
        put(m.folder.profileImagePath, java.io.File(dir, m.folder.profileImagePath).absolutePath)
        put(m.folder.featureBannerImagePath, java.io.File(dir, m.folder.featureBannerImagePath).absolutePath)
    }
    runPipeline(text, RcloneClient(remote = "r2staging", bucket = "stillscenes-content-staging"),
                CdnReadBackVerifier(), renditions)
    println("Published version ${m.version}. Flip RC catalog_version to ${m.version} to go live.")
}
```

Add to `content-pipeline.gradle.kts`:
```kotlin
// after the plugins block
plugins { application }
application { mainClass.set("wallapp.pipeline.MainKt") }
```
(If combining `plugins` blocks errors, merge `application` into the single existing `plugins { }`.)

- [ ] **Step 4: Run to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*PipelineEndToEndTest*'`
Expected: PASS. Then full module: `./gradlew :service:content-pipeline:test` → PASS.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline
git commit -m "feat(pipeline): wire build→validate→publish entrypoint"
```

---

### Task 10: Python generation + render script

**Files:**
- Create: `script/content_pipeline/generate.py`
- Create: `script/content_pipeline/README.md`
- Test: `script/content_pipeline/test_generate.py`

**Interfaces:**
- Produces: renditions on disk + `manifest.json` matching Task 2's schema. Consumes the BFL API (mockable) + local `realesrgan-ncnn-vulkan` + Pillow.

- [ ] **Step 1: Write the failing test** (pure functions; no network)

```python
# script/content_pipeline/test_generate.py
import json, os, tempfile
from generate import build_manifest, render_webp, RENDITION_MAX

def test_build_manifest_shape():
    wps = [{"id": "stillscenes_1a2b3c4d", "label": "Aurora 01", "is_dark": True,
            "width": 1440, "height": 3120, "tags": ["dark"], "styles": ["amoled"], "colors": ["dark"]}]
    m = build_manifest("20260708-01", "https://media-staging.stillscenes.app", wps)
    assert m["version"] == "20260708-01"
    w = m["wallpapers"][0]
    assert w["downloadRenditionPath"] == "media/stillscenes_1a2b3c4d/download.webp"
    assert w["previewRenditionPath"] == "media/stillscenes_1a2b3c4d/preview.webp"
    assert m["artist"]["id"] == "stillscenes" and m["folder"]["id"] == "f~justadded"

def test_render_webp_strips_metadata_and_fits_max(tmp_path):
    from PIL import Image
    src = tmp_path / "master.png"; Image.new("RGB", (2000, 4000), (10, 10, 10)).save(src)
    out = tmp_path / "download.webp"
    render_webp(str(src), str(out), max_w=1440, max_h=3120, quality=92)
    im = Image.open(out)
    assert im.width <= 1440 and im.height <= 3120           # fit within max
    assert not im.info.get("exif") and not im.info.get("icc_profile")   # metadata stripped
```

- [ ] **Step 2: Run to verify it fails**

Run: `cd script/content_pipeline && python -m pytest test_generate.py -q`
Expected: FAIL — `ModuleNotFoundError: generate` / functions undefined.

- [ ] **Step 3: Implement** (generation via BFL is real but isolated in `generate_candidates`; the tested functions are pure)

```python
# script/content_pipeline/generate.py
# pip install pillow requests
import hashlib, io, json, os, sys, time
from PIL import Image

RENDITION_MAX = {"download": (1440, 3120), "preview": (1080, 2160)}  # preview fits p~five0

def rid(seed: str) -> str:
    return "stillscenes_" + hashlib.sha256(seed.encode()).hexdigest()[:8]

def build_manifest(version: str, base_url: str, wallpapers: list) -> dict:
    wps = []
    for w in wallpapers:
        wid = w["id"]
        wps.append({
            "id": wid, "label": w["label"], "isDark": bool(w["is_dark"]),
            "width": w["width"], "height": w["height"],
            "downloadRenditionPath": f"media/{wid}/download.webp",
            "previewRenditionPath": f"media/{wid}/preview.webp",
            "styles": w.get("styles", []), "tags": w.get("tags", []), "colors": w.get("colors", []),
        })
    return {
        "version": version, "baseUrl": base_url,
        "artist": {"id": "stillscenes", "label": "StillScenes", "profileImagePath": "media/artist/stillscenes/profile.webp"},
        "folder": {"id": "f~justadded", "title": "Just Added",
                   "profileImagePath": "media/folder/justadded/profile.webp",
                   "featureBannerImagePath": "media/folder/justadded/banner.webp"},
        "wallpapers": wps,
    }

def render_webp(src_path: str, out_path: str, max_w: int, max_h: int, quality: int = 92) -> None:
    im = Image.open(src_path).convert("RGB")           # drop alpha; opaque
    im.thumbnail((max_w, max_h), Image.LANCZOS)        # fit within max, preserve aspect
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    # No exif/icc passed → metadata stripped. q92 + method=6 to limit dark-gradient banding.
    im.save(out_path, "WEBP", quality=quality, method=6)

def generate_candidates(prompts: list, out_dir: str) -> list:
    """Real BFL FLUX.2 [klein] async calls. Isolated so unit tests don't hit the network."""
    import requests
    key = os.environ["BFL_API_KEY"]
    results = []
    for i, prompt in enumerate(prompts):
        r = requests.post("https://api.bfl.ml/v1/flux-2-klein",
                          headers={"x-key": key}, json={"prompt": prompt}).json()
        poll = r["polling_url"]
        for _ in range(120):                            # ~60s; sample URL expires in 10 min
            time.sleep(0.5)
            res = requests.get(poll, headers={"x-key": key}).json()
            status = res.get("status")
            if status == "Ready":
                url = res["result"]["sample"]
                img = requests.get(url).content         # download within the 10-min window
                p = os.path.join(out_dir, f"cand_{i}.png"); open(p, "wb").write(img); results.append(p); break
            if status in ("Error", "Failed", "Content Moderated"):
                print(f"skip prompt {i}: {status}", file=sys.stderr); break
            if res.get("status") == "Request Moderated":
                print(f"skip prompt {i}: moderated", file=sys.stderr); break
        # 429 handling: requests could add capped backoff here if r.status_code == 429
    return results

# Curation is manual: you copy chosen candidates to masters/<id>/upscaled.png,
# upscale with `realesrgan-ncnn-vulkan -n realesrgan-x2plus -i cand.png -o upscaled.png`,
# then run this to render + emit manifest. See README.md.
if __name__ == "__main__":
    print("See README.md — curate masters/, then call build_manifest + render_webp per wallpaper.")
```

`README.md` documents: `BFL_API_KEY` env var (never commit), the `realesrgan-ncnn-vulkan -n realesrgan-x2plus` upscale command, the OLED-gradient eyeball check, uploading `masters/` to the `stillscenes-masters` bucket via rclone, and running `./gradlew :service:content-pipeline:run --args="<manifest.json>"`.

- [ ] **Step 4: Run to verify it passes**

Run: `cd script/content_pipeline && python -m pytest test_generate.py -q`
Expected: PASS (2 passed).

- [ ] **Step 5: Commit**

```bash
git add script/content_pipeline
git commit -m "feat(pipeline): python generate + render script with manifest emit"
```

---

## Self-Review

**Spec coverage** (each spec §5 stage → task):
- Generate + upscale + masters upload → Task 10 (+ README for the manual upscale/upload steps). ✔
- RENDER (renditions, fit-within-max, EXIF strip, WebP q92) → Task 10 (`render_webp`). ✔ *(moved to Python — Architecture note)*
- BUILD (media map keyed by Long, catalog required arrays, search + titleSuggestions) → Tasks 4/5/6. ✔
- VALIDATE (strict decode incl. media-not-via-fromJson, referential media-id subset + inner keys, content↔search equality, imgix invariant) → Task 7. ✔
- PUBLISH (write-once version, strict order, rclone, CDN read-back) → Tasks 8/9. ✔
- Exit criteria (one `run` produces+validates+publishes; RC flip prints) → Task 9 `main`. ✔
- Deferred items (§12) correctly absent (per-class renditions, telemetry, dedup, prod). ✔

**Gaps to close during execution (flagged, not placeholders):**
- Task 1 version-catalog alias for Ktor CIO (`libs.ktor.client.cio`) — confirm the exact alias in `gradle/libs.versions.toml` at execution.
- Task 8 real-run read-back compares against the actual uploaded file bytes (hash the local file), not `localPath.toByteArray()` — that shortcut exists only so the in-memory fake round-trips; noted inline.
- BFL endpoint path/response shape in `generate_candidates` is best-effort from docs — verify against the live BFL API on first real call (unit tests don't depend on it).

**Type consistency:** `mediaId(seed)` seeds are identical across `MediaMapBuilder` and `CatalogBuilder` (`"<id>:download"`, `"<id>:preview"`, `"<artistId>:profile"`, `"<folderId>:profile"|":banner"`) — verified they match so ids join. Model constructor calls use the exact required fields from the live models (Task-time reads).

## Execution Handoff

**Plan complete and saved to `docs/superpowers/plans/2026-07-08-content-pipeline-slice.md`.**
