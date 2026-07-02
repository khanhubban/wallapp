# Foundation & Delivery Path Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** The app runs end-to-end against our own Firebase project and fetches its catalog (existing demo wire format) from our R2 staging bucket over plain HTTPS, with the catalog version selected by a Remote Config parameter.

**Architecture:** Three changes, all following patterns that already exist in the codebase. (1) Foundation: our own Firebase project + Cloudflare zone/R2 buckets replace `panels-oss`. (2) Fetch path: the wired default catalog/media-map/search fetchers go through the Firebase Storage SDK + AES decryption (`RemoteApiDefault`); we swap DI wiring to plain-HTTPS Ktor repositories (`NetworkContentRepositoryKtor` already exists; we clone it for media-map and search). (3) Version pointer: the `RemoteEndpoints` spec (today fetched as `api/v0/spec.json` via Storage) is instead **constructed locally** from a Remote Config `catalog_version` param — RC percentage conditions give us canary/rollback per the delivery design.

**Tech Stack:** Kotlin Multiplatform, Ktor client, kotlinx.serialization, Koin DI, kotlin.test + turbine (desktop/JVM via `./script/test_unit`), Firebase (Auth/Firestore/RC/Crashlytics), Cloudflare R2 + CDN, rclone.

## Global Constraints

- Catalog JSON decode config MUST be `Json { ignoreUnknownKeys = true; coerceInputValues = true }` (spec §3.4).
- Every non-essential catalog model property has a default; nullable-without-default and enum-without-default are review blockers (spec §3.4).
- No `@ExperimentalSerializationApi` in catalog models (spec §3.4).
- No credentials in any repo; R2 tokens are bucket-scoped, stored in CI secrets / local rclone config only (delivery §8).
- Versioned R2 objects get `Cache-Control: public, max-age=31536000, immutable` at upload time (delivery §6).
- Commit style (doc/committing-code.md): subject ≤ 50 chars, capitalized; atomic commits; app compiles before and after every commit.
- Unit test runner: `./script/test_unit` (runs `:shared:app:app-adapter:desktopTest`). Android compile check: `./gradlew :app:android:assembleDebug`.
- Work on branch `feat/foundation-delivery` (created in Task 0).
- Operator inputs needed once: the app's domain (for `media.<domain>`), Firebase project name, Cloudflare account. Marked **[OPERATOR]** where required.

## File Structure

```
NEW  shared/data/remoteendpoint/src/commonMain/kotlin/wallapp/remoteendpoint/ContentDeliveryConfig.kt
NEW  shared/data/remoteendpoint/src/commonMain/kotlin/wallapp/remoteendpoint/RemoteEndpointsRepositoryRemoteConfig.kt
NEW  shared/data/mediamap/src/commonMain/kotlin/wallapp/media/network/repository/NetworkMediaMapRepositoryKtor.kt
NEW  shared/data/search-network/src/commonMain/kotlin/wallapp/search/network/repository/NetworkSearchContentRepositoryKtor.kt
NEW  script/firebase_deploy_rules
NEW  shared/app/app-adapter/src/commonTest/kotlin/wallapp/content/NetworkContentCompatTest.kt
NEW  shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/RemoteEndpointsRemoteConfigTest.kt
NEW  shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/KtorRepositoriesTest.kt
MOD  shared/data/content-network/.../model/NetworkContent.kt        (Json config)
MOD  shared/data/content-network/.../model/NetworkMedia.kt          (blurHash default)
MOD  shared/data/remoteconfig-api/.../data/RemoteConfigKey.kt        (+CatalogVersion)
MOD  shared/data/remoteconfig-api/.../data/RemoteConfigEntry.kt      (+CatalogVersion)
MOD  shared/data/remoteconfig-api/.../data/RemoteConfigData.kt       (+catalogVersion)
MOD  shared/data/remoteconfig-api/.../data/RemoteConfigDataDefaultsProvider.kt (+catalogVersion)
MOD  shared/data/remoteconfig-api/.../data/RemoteConfigDataMock.kt   (+catalogVersion)
MOD  shared/data/remoteconfig-firebase/.../RemoteConfigDataDefault.kt (+catalogVersion)
MOD  shared/di/di-base/src/commonMain/kotlin/wallapp/di/FactoryCommon.kt (wiring swaps)
MOD  app/android/google-services.json                                (new project)
MOD  app/ios/wallApp/Firebase/GoogleService-Info.plist               (new project)
MOD  app/ios/wallApp-dev/Firebase/GoogleService-Info.plist           (new project)
MOD  shared/data/account/src/androidMain/.../google/GoogleSignInFactory.kt:13 (client ID)
MOD  app/ios/wallApp/Info-Prod.plist:144-147                         (URL scheme)
MOD  app/ios/wallApp-dev/Info-Dev.plist:153-156                      (URL scheme; fix Dev/Prod mismatch)
MOD  service/service-common/src/main/kotlin/wallapp/service/Constant.kt:4-5 (project refs)
MOD  .firebaserc, firebase-backend/.firebaserc                       (project id)
```

---

### Task 0: Branch

- [ ] **Step 1: Create the working branch**

```bash
cd /Users/hubbankhan/StudioProjects/wallapp
git checkout main && git merge docs/ship-plan-spec   # bring specs to main first if not merged
git checkout -b feat/foundation-delivery
```

Expected: `Switched to a new branch 'feat/foundation-delivery'`. (If the spec branch is unmerged and you prefer not to merge yet, branch from `docs/ship-plan-spec` instead.)

---

### Task 1: Create Firebase project + register apps **[OPERATOR]**

**Files:** none (console work; files land in Task 3).

**Interfaces:**
- Produces: `google-services.json` + two `GoogleService-Info.plist` files for the new project, saved OUTSIDE the repo (e.g. `~/Downloads/firebase-new/`), plus the new Web client ID and both REVERSED_CLIENT_IDs.

- [ ] **Step 1: Create project** — In https://console.firebase.google.com create project (suggested id pattern: `<appname>-prod`). Disable Google Analytics or enable — either is fine (Analytics is in spec §5.4 observability; enabling now saves a later step).
- [ ] **Step 2: Register three apps** —
  - Android app: package `com.example.wallapp` (current applicationId; the rebrand plan re-registers later). Add the debug-keystore SHA-1: get it with `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android | grep SHA1`. SHA-1 is REQUIRED for Google Sign-In.
  - iOS app #1: bundle id `com.example.Wallapp` (Prod target).
  - iOS app #2: bundle id `com.example.development.Wallapp` (Dev target).
- [ ] **Step 3: Enable services** — Authentication (providers: Anonymous, Google, Apple), Firestore (production mode, region of choice — pick once, it's permanent), Remote Config, Storage, Crashlytics. (Checklist source: `doc/firebase-usage.md`.)
- [ ] **Step 4: Create the RC parameter** — Remote Config → new parameter `catalog_version`, default value `99999999` (the demo catalog version; revisit before launch), publish.
- [ ] **Step 5: Download all three config files** to `~/Downloads/firebase-new/`. Verify project id in each:

```bash
jq -r .project_info.project_id ~/Downloads/firebase-new/google-services.json
/usr/libexec/PlistBuddy -c "Print :PROJECT_ID" ~/Downloads/firebase-new/GoogleService-Info-prod.plist
/usr/libexec/PlistBuddy -c "Print :PROJECT_ID" ~/Downloads/firebase-new/GoogleService-Info-dev.plist
```

Expected: all three print the new project id (NOT `panels-oss`).

---

### Task 2: Deploy Firestore/Storage rules + deploy script

**Files:**
- Modify: `.firebaserc`, `firebase-backend/.firebaserc`
- Create: `script/firebase_deploy_rules`

**Interfaces:**
- Consumes: new Firebase project id from Task 1.
- Produces: deployed rules; `script/firebase_deploy_rules` for repeatable deploys.

- [ ] **Step 1: Point both .firebaserc files at the new project** — replace `"default": "panels-oss"` with `"default": "<new-project-id>"` in both files.
- [ ] **Step 2: Write the deploy script** — Create `script/firebase_deploy_rules` (mode 755):

```bash
#!/usr/bin/env bash
# Deploys Firestore + Storage rules and indexes from firebase-backend/.
set -euo pipefail
cd "$(dirname "$0")/../firebase-backend"
firebase deploy --only firestore:rules,firestore:indexes,storage
```

- [ ] **Step 3: Run rules tests against the emulator** (regression before deploy):

```bash
cd firebase-backend && firebase emulators:start --only auth,firestore,storage &
sleep 10 && cd test && npm install && npm test; kill %1
```

Expected: rules test suite passes (see `firebase-backend/README.md`).
- [ ] **Step 4: Deploy** — `firebase login` (if needed), then `./script/firebase_deploy_rules`. Expected: `✔ Deploy complete!`
- [ ] **Step 5: Commit**

```bash
git add .firebaserc firebase-backend/.firebaserc script/firebase_deploy_rules
git commit -m "Point Firebase config at own project, add deploy script"
```

---

### Task 3: Swap client Firebase config + sign-in IDs

**Files:**
- Modify: `app/android/google-services.json` (replace file)
- Modify: `app/ios/wallApp/Firebase/GoogleService-Info.plist` (replace file)
- Modify: `app/ios/wallApp-dev/Firebase/GoogleService-Info.plist` (replace file)
- Modify: `shared/data/account/src/androidMain/kotlin/wallapp/account/google/GoogleSignInFactory.kt:13`
- Modify: `app/ios/wallApp/Info-Prod.plist:144-147`, `app/ios/wallApp-dev/Info-Dev.plist:153-156`
- Modify: `service/service-common/src/main/kotlin/wallapp/service/Constant.kt:4-5`

- [ ] **Step 1: Replace the three config files** with the Task-1 downloads (keep exact filenames/paths).
- [ ] **Step 2: Update GoogleSignInFactory** — at `GoogleSignInFactory.kt:13`, replace the hardcoded `550120118652-jef7....apps.googleusercontent.com` with the new project's **Web client** OAuth ID (Firebase console → Authentication → Sign-in method → Google → Web SDK configuration; it is also `client_type: 3` inside the new google-services.json).
- [ ] **Step 3: Update iOS URL schemes** — in `Info-Prod.plist:144-147` put the new Prod plist's `REVERSED_CLIENT_ID`; in `Info-Dev.plist:153-156` put the new **Dev** plist's `REVERSED_CLIENT_ID`. This also fixes the pre-existing bug where the Dev plist carried the Prod reversed id.
- [ ] **Step 4: Update service constants** — `Constant.kt:4-5`: `AppFirebasePath = "https://<new-project-id>.firebaseio.com/"`, `AppStorageBucketName = "<new-project-id>.firebasestorage.app"` (copy exact bucket name from the new google-services.json `storage_bucket` field).
- [ ] **Step 5: Verify no stale refs and build**

```bash
grep -rn "panels-oss" --include='*.kt' --include='*.json' --include='*.plist' --include='.firebaserc' . | grep -v build | grep -v doc/ | grep -v learn/ | grep -v demo-assets
./gradlew :app:android:assembleDebug && ./script/test_unit
```

Expected: grep returns only `androidInstrumentedTest` files (acceptable — update them too if trivial) and docs; build + tests pass.
- [ ] **Step 6: Manual smoke [OPERATOR]** — run the Android app on a device/emulator; verify it boots and anonymous auth works (Crashlytics console shows the session). Google Sign-In should show the account picker (needs the SHA-1 from Task 1).
- [ ] **Step 7: Commit** — `git add -A && git commit -m "Swap Firebase config to own project"`

---

### Task 4: Cloudflare zone, R2 buckets, domains, WAF carve-out **[OPERATOR]**

**Files:** none in-repo (cloud config). Local: `~/.config/rclone/rclone.conf` gains an `r2-wallapp` remote (never committed).

**Interfaces:**
- Produces: `https://media-staging.<domain>` serving the staging bucket through Cloudflare cache; bucket names `<app>-content-prod`, `<app>-content-staging`, `<app>-masters`; a bucket-scoped R&W token for staging.

- [ ] **Step 1: Zone** — add the chosen domain to Cloudflare (or use the existing zone if the domain lives there).
- [ ] **Step 2: Buckets** — R2 → create `<app>-content-prod`, `<app>-content-staging`, `<app>-masters`, location hint APAC (delivery §4).
- [ ] **Step 3: Custom domains** — attach `media.<domain>` to prod and `media-staging.<domain>` to staging (R2 bucket → Settings → Custom Domains). Confirm "Cloudflare Cache" is enabled on both. Turn ON Smart Tiered Cache (zone → Caching → Tiered Cache).
- [ ] **Step 4: WAF carve-out (MANDATORY, delivery §5)** — zone → Security → WAF → create rule: expression `http.host in {"media.<domain>" "media-staging.<domain>"}` → action **Skip** (all remaining security products, incl. Bot Fight Mode).
- [ ] **Step 5: Scoped token** — R2 → Manage API Tokens → create token, permission Object Read & Write, scoped ONLY to `<app>-content-staging`. Configure rclone locally:

```
[r2-wallapp]
type = s3
provider = Cloudflare
access_key_id = <token key id>
secret_access_key = <token secret>
endpoint = https://<account-id>.r2.cloudflarestorage.com
```

- [ ] **Step 6: Verify end-to-end**

```bash
echo '{"ok":true}' > /tmp/r2check.json
rclone copyto /tmp/r2check.json r2-wallapp:<app>-content-staging/health/r2check.json \
  --header-upload "Cache-Control: public, max-age=31536000, immutable" \
  --header-upload "Content-Type: application/json"
curl -sI https://media-staging.<domain>/health/r2check.json | grep -Ei "HTTP|cf-cache-status|cache-control|content-type"
curl -sI https://media-staging.<domain>/health/r2check.json | grep -i cf-cache-status
```

Expected: first curl `HTTP/2 200`, `cache-control: public, max-age=31536000, immutable`, `content-type: application/json`, `cf-cache-status: MISS`; second curl `cf-cache-status: HIT`.

---

### Task 5: Serialization compliance (TDD)

**Files:**
- Test: `shared/app/app-adapter/src/commonTest/kotlin/wallapp/content/NetworkContentCompatTest.kt` (create)
- Modify: `shared/data/content-network/src/commonMain/kotlin/wallapp/content/network/model/NetworkMedia.kt:15`
- Modify: `shared/data/content-network/src/commonMain/kotlin/wallapp/content/network/model/NetworkContent.kt:18`

**Interfaces:**
- Produces: `NetworkContent.fromExportString(s: String): NetworkContent` (unchanged signature) now tolerant of missing-defaulted fields, unknown keys, and null-for-defaulted values.

- [ ] **Step 1: Write the failing tests**

```kotlin
package wallapp.content

import kotlinx.serialization.json.Json
import wallapp.content.network.model.NetworkContent
import wallapp.content.network.model.NetworkMedia
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NetworkContentCompatTest {

    // Forward-compat guard: unknown keys anywhere must be ignored (already true; regression lock).
    @Test
    fun contentDecode_ignoresUnknownKeys() {
        val json = """{"wallpapers":[],"categories":[],"artists":[],"folders":[],"futureField":{"x":1}}"""
        val content = NetworkContent.fromExportString(json)
        assertEquals(0, content.wallpapers.size)
    }

    // blurHash is nullable but has no default today -> missing key throws. Spec §3.4 forbids that.
    @Test
    fun mediaDecode_toleratesMissingBlurHash() {
        val media = Json.decodeFromString<NetworkMedia>("""{"id":42}""")
        assertNull(media.blurHash)
    }

    // coerceInputValues: null for a defaulted non-nullable field must coerce to the default.
    @Test
    fun mediaDecode_coercesNullToDefault_viaContentJson() {
        val json = """{"wallpapers":[],"categories":[],"artists":[],"folders":[],"probe":null}"""
        // Full-model coercion is exercised in Task 9's fixture test; here we lock the Json flags:
        val content = NetworkContent.fromExportString(json)
        assertEquals(0, content.folders.size)
    }
}
```

- [ ] **Step 2: Run to verify failure** — `./script/test_unit`
Expected: `mediaDecode_toleratesMissingBlurHash` FAILS with `MissingFieldException: Field 'blurHash' is required`. The other two pass (regression locks).
- [ ] **Step 3: Fix the models** — In `NetworkMedia.kt:15` change `val blurHash: String?,` → `val blurHash: String? = null,`. In `NetworkContent.kt:18` change `private val json = Json { ignoreUnknownKeys = true }` → `private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }`.
- [ ] **Step 4: Run to verify pass** — `./script/test_unit` → all tests PASS.
- [ ] **Step 5: Commit** — `git add -A && git commit -m "Harden catalog model decode compatibility"`

---

### Task 6: Remote Config `catalog_version` param (TDD)

**Files:**
- Test: `shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/RemoteEndpointsRemoteConfigTest.kt` (create; also used by Task 7)
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigKey.kt`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigEntry.kt`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigData.kt`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigDataDefaultsProvider.kt`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigDataMock.kt`
- Modify: `shared/data/remoteconfig-firebase/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigDataDefault.kt`

**Interfaces:**
- Produces: `RemoteConfigData.catalogVersion: StateFlow<String>` (default `"99999999"`); `RemoteConfigEntry.CatalogVersion` with key `"catalog_version"`.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.remoteapi

import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProviderDefault
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteconfig.data.RemoteConfigEntry
import kotlin.test.Test
import kotlin.test.assertEquals

class CatalogVersionConfigTest {

    @Test
    fun catalogVersionEntry_hasStableKeyAndDefault() {
        assertEquals("catalog_version", RemoteConfigEntry.CatalogVersion.remoteConfigKey)
        assertEquals("99999999", RemoteConfigEntry.CatalogVersion.defaultValue)
    }

    @Test
    fun mockData_exposesCatalogVersionDefault() {
        val data = RemoteConfigDataMock(RemoteConfigDataDefaultsProviderDefault())
        assertEquals("99999999", data.catalogVersion.value)
    }
}
```

Note: `RemoteConfigEntry`'s property names for key/default may differ (`key`/`default`); on compile error, match the existing `ImageHostName` entry's property names exactly — the assertion intent is identical.

- [ ] **Step 2: Run to verify failure** — `./script/test_unit` → FAILS: `Unresolved reference: CatalogVersion` / `catalogVersion`.
- [ ] **Step 3: Implement, mirroring `ImageHostName` in each file:**
  - `RemoteConfigKey.kt` (after line 17): `CatalogVersion("catalog_version"),`
  - `RemoteConfigDataDefaultsProvider.kt` (near `imageHostName`, line ~28): `open val catalogVersion: String get() = "99999999"`
  - `RemoteConfigEntry.kt` (after `ImageHostName`, line 66-69):
    ```kotlin
    data object CatalogVersion : RemoteConfigEntry<String>(
        RemoteConfigKey.CatalogVersion.key,
        Provider.catalogVersion,
    )
    ```
  - `RemoteConfigData.kt` (alphabetical, near line 13): `val catalogVersion: StateFlow<String>`
  - `RemoteConfigDataMock.kt` (near line 33): `override val catalogVersion: StateFlow<String> = MutableStateFlow(provider.catalogVersion)`
  - `RemoteConfigDataDefault.kt`: add the override following the `imageHostName` accessor pattern at lines 42-77 (same builder/helper used by the neighboring String params).
- [ ] **Step 4: Run to verify pass** — `./script/test_unit` → PASS. Also `./gradlew :app:android:assembleDebug` (iOS/desktop actuals compile via the shared defaults provider — no per-platform work).
- [ ] **Step 5: Commit** — `git add -A && git commit -m "Add catalog_version Remote Config param"`

---

### Task 7: `RemoteEndpointsRepositoryRemoteConfig` (TDD)

**Files:**
- Create: `shared/data/remoteendpoint/src/commonMain/kotlin/wallapp/remoteendpoint/ContentDeliveryConfig.kt`
- Create: `shared/data/remoteendpoint/src/commonMain/kotlin/wallapp/remoteendpoint/RemoteEndpointsRepositoryRemoteConfig.kt`
- Test: `shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/RemoteEndpointsRemoteConfigTest.kt` (extend)

**Interfaces:**
- Consumes: `RemoteConfigData.catalogVersion: StateFlow<String>` (Task 6); `RemoteEndpointsRepositoryNetwork` interface (`getRemoteEndpoints(track, forceRefresh, forceCache): Flow<RemoteEndpoints?>`); `RemoteEndpoints(content, search, media: RemoteEndpointMediaMap)`.
- Produces: `RemoteEndpointsRepositoryRemoteConfig(catalogVersion: StateFlow<String>, config: ContentDeliveryConfig)` implementing `RemoteEndpointsRepositoryNetwork`; `ContentDeliveryConfig(baseUrl: String)`.

- [ ] **Step 1: Write the failing test** (append to `RemoteEndpointsRemoteConfigTest.kt`)

```kotlin
package wallapp.remoteapi

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import wallapp.remoteendpoint.ContentDeliveryConfig
import wallapp.remoteendpoint.RemoteEndpointTrack
import wallapp.remoteendpoint.RemoteEndpointsRepositoryRemoteConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteEndpointsRemoteConfigTest {

    private val config = ContentDeliveryConfig(baseUrl = "https://media-staging.example.com")

    @Test
    fun buildsAbsoluteEndpointsFromVersion() = runTest {
        val version = MutableStateFlow("99999999")
        val repo = RemoteEndpointsRepositoryRemoteConfig(version, config)
        repo.getRemoteEndpoints(RemoteEndpointTrack.entries.first()).test {
            val endpoints = awaitItem()!!
            assertEquals("https://media-staging.example.com/api/99999999/content-1a", endpoints.content)
            assertEquals("https://media-staging.example.com/api/99999999/content-metadata-1a", endpoints.search)
            assertEquals(
                "https://media-staging.example.com/api/99999999/media-1a-c-p~s",
                endpoints.media.getEndpoint("c", "p~s"),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun blankVersionEmitsNothing() = runTest {
        val version = MutableStateFlow("")
        val repo = RemoteEndpointsRepositoryRemoteConfig(version, config)
        repo.getRemoteEndpoints(RemoteEndpointTrack.entries.first()).test {
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

Note: if `RemoteEndpointTrack` is not an enum, pass whatever preset value `RemoteEndpointTrack.kt` exposes — the implementation ignores it.

- [ ] **Step 2: Run to verify failure** — `./script/test_unit` → FAILS: unresolved references.
- [ ] **Step 3: Implement**

`ContentDeliveryConfig.kt`:
```kotlin
package wallapp.remoteendpoint

/**
 * Base URL of the content CDN (R2 custom domain). Versioned paths are appended:
 * "$baseUrl/api/<catalogVersion>/...". See delivery design §4.
 */
data class ContentDeliveryConfig(
    val baseUrl: String,
)
```

`RemoteEndpointsRepositoryRemoteConfig.kt`:
```kotlin
package wallapp.remoteendpoint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Builds [RemoteEndpoints] locally from the Remote Config catalog version instead of
 * downloading api/v0/spec.json. The RC param is the version pointer (delivery design §7);
 * its percentage conditions provide canary and rollback.
 */
class RemoteEndpointsRepositoryRemoteConfig(
    private val catalogVersion: StateFlow<String>,
    private val config: ContentDeliveryConfig,
) : RemoteEndpointsRepositoryNetwork {

    override fun getRemoteEndpoints(
        track: RemoteEndpointTrack,
        forceRefresh: Boolean,
        forceCache: Boolean,
    ): Flow<RemoteEndpoints?> =
        catalogVersion
            .filter { it.isNotBlank() }
            .map { version -> endpointsFor(version) }

    private fun endpointsFor(version: String): RemoteEndpoints {
        val root = "${config.baseUrl}/api/$version"
        return RemoteEndpoints(
            content = "$root/content-1a",
            search = "$root/content-metadata-1a",
            media = RemoteEndpointMediaMap(
                root = "$root/media-1a",
                imageHostPlatformKeys = listOf("i", "c"),
                imageBucketSpecKeys = listOf(
                    "p~s", "p~five0", "p~a~n", "p~a~xl", "p~uhd", "f~fo", "t~s", "t~m", "t~l",
                ),
            ),
        )
    }
}
```

(The platform/bucket key lists replicate `demo-assets/api/99999999/spec.json` exactly — the demo wire format v1 contract. The pipeline plan later owns generating these.)

- [ ] **Step 4: Run to verify pass** — `./script/test_unit` → PASS.
- [ ] **Step 5: Commit** — `git add -A && git commit -m "Add RC-driven remote endpoints repository"`

---

### Task 8: Ktor fetchers for media-map and search (TDD)

**Files:**
- Create: `shared/data/mediamap/src/commonMain/kotlin/wallapp/media/network/repository/NetworkMediaMapRepositoryKtor.kt`
- Create: `shared/data/search-network/src/commonMain/kotlin/wallapp/search/network/repository/NetworkSearchContentRepositoryKtor.kt`
- Test: `shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/KtorRepositoriesTest.kt`
- Modify: `gradle/libs.versions.toml` + `shared/app/app-adapter/app-adapter.gradle.kts` (add `ktor-client-mock` to commonTest) — check first: `grep -n "ktor" gradle/libs.versions.toml`; add `ktor-client-mock = { module = "io.ktor:ktor-client-mock", version.ref = "<existing ktor version ref>" }` and `implementation(libs.ktor.client.mock)` in the app-adapter commonTest deps block (lines ~78-86).

**Interfaces:**
- Consumes: `NetworkMediaMapRepository` (`fetchNetworkMediaMap(forceRefresh): Flow<NetworkMediaMapResult>`), `NetworkMediaMapRepositoryConfig.endpointUrl: Flow<String>`, `NetworkMediaData.fromJson(String)`; `NetworkSearchContentRepository` (`fetchNetworkContent(forceRefresh): Flow<NetworkSearchMetadata?>`), `NetworkSearchContentRepositoryConfig.endpointUrl`, `NetworkSearchMetadata.fromExportString(String)`.
- Produces: `NetworkMediaMapRepositoryKtor(config, httpClient)`, `NetworkSearchContentRepositoryKtor(config, httpClient)` — drop-in DI replacements.

- [ ] **Step 1: Write the failing test**

```kotlin
package wallapp.remoteapi

import app.cash.turbine.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import wallapp.media.network.model.NetworkMediaMapResult
import wallapp.media.network.repository.NetworkMediaMapRepositoryKtor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorRepositoriesTest {

    private fun clientReturning(body: String) = HttpClient(MockEngine { _ ->
        respond(body, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    })

    @Test
    fun mediaMapKtor_fetchesAndParses() = runTest {
        val url = MutableStateFlow("https://media-staging.example.com/api/99999999/media-1a-c-p~s")
        val body = """{"version":1,"data":{"7":{"am":"https://cdn.example.com/a.png"}}}"""
        val repo = NetworkMediaMapRepositoryKtor(
            config = object : NetworkMediaMapRepositoryConfig {
                override val endpointUrl = url
            },
            httpClient = clientReturning(body),
        )
        repo.fetchNetworkMediaMap(forceRefresh = false).test {
            val result = awaitItem()
            assertTrue(result is NetworkMediaMapResult.Success)
            assertEquals(url.value, (result as NetworkMediaMapResult.Success).sourceId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchKtor_emitsNullOnServerError() = runTest {
        val url = MutableStateFlow("https://media-staging.example.com/api/99999999/content-metadata-1a")
        val failingClient = HttpClient(MockEngine { _ ->
            respond("boom", HttpStatusCode.InternalServerError)
        })
        val repo = NetworkSearchContentRepositoryKtor(
            config = object : NetworkSearchContentRepositoryConfig {
                override val endpointUrl = url
            },
            httpClient = failingClient,
        )
        repo.fetchNetworkContent(forceRefresh = false).test {
            assertEquals(null, awaitItem())   // null-on-error contract, matching the Network impl
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

(Add imports `wallapp.media.network.repository.NetworkMediaMapRepositoryConfig`, `wallapp.search.network.repository.NetworkSearchContentRepositoryConfig`, `wallapp.search.network.repository.NetworkSearchContentRepositoryKtor` as the compiler demands. The search success path needs a valid `NetworkSearchMetadata` fixture whose shape lives in `shared/data/search-model` — it is exercised end-to-end in Task 10 instead of unit-fixtured here.)

- [ ] **Step 2: Run to verify failure** — `./script/test_unit` → FAILS: unresolved `NetworkMediaMapRepositoryKtor`.
- [ ] **Step 3: Implement** — both classes are line-for-line clones of `NetworkContentRepositoryKtor` (shared/data/content-network/.../NetworkContentRepositoryKtor.kt:20-67) with these substitutions:

`NetworkMediaMapRepositoryKtor.kt` — constructor `(private val config: NetworkMediaMapRepositoryConfig, private val httpClient: HttpClient)`, implements `NetworkMediaMapRepository`; `endpointUrlFlow = config.endpointUrl` filtered by `isNotBlank()`; success branch:
```kotlin
return try {
    NetworkMediaMapResult.Success(
        networkMediaData = NetworkMediaData.fromJson(body),
        sourceId = endpointUrl,
    )
} catch (e: SerializationException) {
    error("Error parsing media map from network: $e")
}
```
with the same non-200/IOException/Exception error handling as the content Ktor repo, returning `NetworkMediaMapResult.Error(message)`.

`NetworkSearchContentRepositoryKtor.kt` — constructor `(private val config: NetworkSearchContentRepositoryConfig, private val httpClient: HttpClient)`, implements `NetworkSearchContentRepository`; returns `NetworkSearchMetadata.fromExportString(body)` on success and `null` on any error (matching `NetworkSearchContentRepositoryNetwork`'s null-on-error contract, lines 35-72).

- [ ] **Step 4: Run to verify pass** — `./script/test_unit` → PASS.
- [ ] **Step 5: Commit** — `git add -A && git commit -m "Add Ktor fetchers for media map and search"`

---

### Task 9: DI wiring swap to the HTTPS delivery path

**Files:**
- Modify: `shared/di/di-base/src/commonMain/kotlin/wallapp/di/FactoryCommon.kt` (three bindings + one new single)
- Possibly modify: `shared/di/di-base/src/commonMain/kotlin/wallapp/di/module/ContentModule.kt` (if bindings live there — locate first)

**Interfaces:**
- Consumes: everything produced by Tasks 6-8.
- Produces: the app's wired-default fetch path is Ktor+HTTPS with RC-driven endpoints.

- [ ] **Step 1: Locate the four bindings**

```bash
grep -n "NetworkContentRepositoryUrlDownloader\|NetworkMediaMapRepositoryNetwork\|NetworkSearchContentRepositoryNetwork\|RemoteEndpointsRepositoryNetworkDefault" \
  shared/di/di-base/src/commonMain/kotlin/wallapp/di/FactoryCommon.kt \
  shared/di/di-base/src/commonMain/kotlin/wallapp/di/module/ContentModule.kt
```

Known anchor: content wiring at `FactoryCommon.kt:410-414` (`NetworkContentRepositoryDefault(repositoryNetwork = NetworkContentRepositoryUrlDownloader(...))`).

- [ ] **Step 2: Swap, preserving each site's surrounding style**
  - Content: replace the `NetworkContentRepositoryUrlDownloader(...)` argument with `NetworkContentRepositoryKtor(remoteApiEndpointRepository = get(), httpClient = get())` (both already in the graph; the Ktor repo is registered unused at `ContentModule.kt:152` — reuse its construction expression).
  - Media map: replace `NetworkMediaMapRepositoryNetwork(...)` with `NetworkMediaMapRepositoryKtor(config = get(), httpClient = get())`.
  - Search: replace `NetworkSearchContentRepositoryNetwork(...)` with `NetworkSearchContentRepositoryKtor(config = get(), httpClient = get())`.
  - Endpoints: replace the `RemoteEndpointsRepositoryNetworkDefault` binding with:
    ```kotlin
    single { ContentDeliveryConfig(baseUrl = "https://media-staging.<domain>") } // TODO(Task 4 domain): prod URL switches per-buildtype in the release plan
    single<RemoteEndpointsRepositoryNetwork> {
        RemoteEndpointsRepositoryRemoteConfig(
            catalogVersion = get<RemoteConfigData>().catalogVersion,
            config = get(),
        )
    }
    ```
    Substitute the real domain chosen in Task 4. (Per-environment prod/staging split is deliberately deferred to the release plan; staging-only is correct for this plan's exit state.)
  - If any of these use a different Koin idiom (e.g. `factory`/named qualifiers), keep that idiom — change only the implementation class.
- [ ] **Step 3: Verify the whole suite + builds** — `./script/test_unit && ./gradlew :app:android:assembleDebug && ./gradlew :app:desktop:compileKotlin`
Expected: all pass. Existing app-adapter tests use `WaeTestModule` fakes and must stay green — if one fails on the swapped binding, it reveals a test that pinned the old impl; update that test's binding to the Ktor variant (same fake HTTP surface).
- [ ] **Step 4: Commit** — `git add -A && git commit -m "Wire HTTPS delivery path with RC version pointer"`

---

### Task 10: Stage demo catalog on R2 + end-to-end smoke **[OPERATOR]**

**Files:** none in-repo.

**Interfaces:**
- Consumes: staging bucket + domain (Task 4), RC param (Task 1/6), wired client (Task 9).
- Produces: the plan's exit state — app content loads from R2.

- [ ] **Step 1: Upload the demo catalog** (plaintext JSON export, verified in recon):

```bash
rclone copy demo-assets/api/99999999 r2-wallapp:<app>-content-staging/api/99999999 \
  --header-upload "Cache-Control: public, max-age=31536000, immutable" \
  --exclude "key" --exclude "spec.json"
rclone ls r2-wallapp:<app>-content-staging/api/99999999 | head
```

(`key` and `spec.json` are dead in the new path — encryption and spec fetch are gone.) Expected: `content-1a`, `content-metadata-1a`, and 18 `media-1a-*` files listed.
- [ ] **Step 2: Verify catalog over the CDN**

```bash
curl -s https://media-staging.<domain>/api/99999999/content-1a | head -c 120
curl -sI https://media-staging.<domain>/api/99999999/media-1a-c-p~s | grep -Ei "HTTP|cache-control|cf-cache-status"
```

Expected: JSON beginning `{"folders":` (or similar); headers show 200 + immutable + cf-cache-status.
- [ ] **Step 3: Run the app** — Android debug build on device/emulator. Expected: wallpaper feed renders demo content; logcat shows `fetchDataNetwork() ... endpointUrl: https://media-staging.<domain>/api/99999999/content-1a` (log line from `NetworkContentRepositoryKtor.kt:39`). Images load (their URLs inside the media maps still point at the public `wallapp-assets` demo bucket — expected until the pipeline plan re-hosts media).
- [ ] **Step 4: Prove the pointer** — In Firebase console, change `catalog_version` to `00000000`, publish, force-refresh the app (reinstall or wait past the RC cache; dev builds use `minimumFetchIntervalInSeconds=0` per `RemoteConfigFirebase.kt:65`). Expected: content fetch fails (version doesn't exist) and the app serves cached content — the graceful-degradation behavior. Restore `99999999`, refresh: content returns. This is the rollback drill in miniature.
- [ ] **Step 5: Commit any straggler fixes and merge** — `git add -A && git commit -m "Complete foundation delivery path"` (only if fixes were needed), then follow superpowers:finishing-a-development-branch.

---

## Exit state

- App authenticates against OUR Firebase project; rules deployed and tested.
- Catalog + media-map + search fetched as plaintext JSON over HTTPS from OUR R2 staging bucket through Cloudflare's CDN, version-selected by the `catalog_version` RC param.
- Storage-SDK + AES fetch classes remain in-tree but unwired (removed in a later cleanup).
- NOT in scope (later plans): content pipeline (generation/validation/renditions/publish), media re-hosting, prod bucket wiring + per-buildtype config, seed catalog bundling, jittered background refresh, retry-policy hardening, canary telemetry events + abort thresholds (delivery §12.4 — must land before the FIRST real canary, i.e. in the pipeline plan), `schemaVersion` freeze behavior (ships with the catalog-v2 format work), rebrand, monetization accounts, store/legal work.
