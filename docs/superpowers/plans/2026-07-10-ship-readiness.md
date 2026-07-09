# Ship Readiness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Take the app from "works on an emulator against staging, packaged as `com.example.wallapp`" to "shippable under its real identity, serving prod content, with malformed catalogs caught before they publish."

**Architecture:** Three independent phases committing separately. Phase 1 changes only build config and Firebase registration — no Kotlin source moves. Phase 2 moves the CDN base URL and the Remote Config catalog-version key out of the shared `ContentModule` and into the existing `di-buildconfig-{debug,release}` seam, so the endpoint builder stays environment-blind. Phase 3 replaces three hand-written copies of the media-map key contract with one shared `MediaEntityKind` enum that the pipeline builder derives from and the validator checks against.

**Tech Stack:** Kotlin Multiplatform, Koin DI, Gradle (JDK 17), Firebase (Auth / Firestore / Remote Config), Cloudflare R2 + Cache Rules, kotlinx.serialization, kotlin.test + Turbine.

**Spec:** `docs/superpowers/specs/2026-07-09-ship-readiness-design.md` (commit `c68b556`)

## Global Constraints

- **Tests, KMP modules under `shared/`: use `:desktopTest`, never `:test`.** `:shared:app:app-adapter:testDebugUnitTest` fails on a clean tree (`Method myPid in android.os.Process not mocked`, via `ConfigValueRepositoryFirebase`). It is pre-existing and unrelated. `:test` runs both, so it always looks red.
- **Tests, `service/content-pipeline`: use `:test`.** It is a plain `kotlin("jvm")` module with `tasks.test { useJUnitPlatform() }` and **no desktop target**, so `:service:content-pipeline:desktopTest` does not exist. The rule above does not apply to it.
- **Never `git add -A`.** `node_modules/`, `.wrangler/`, and `learn/` are untracked and unignored. Always `git add <explicit paths>`.
- **Gradle is pinned to JDK 17** in `~/.gradle/gradle.properties`. `/usr/libexec/java_home -v 17` does **not** resolve the Homebrew keg; the path is `/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home`.
- **Launch the app with the explicit activity.** `adb shell monkey -c LAUNCHER` starts **LeakCanary's** launcher activity, not the app.
- **The catalog version pinned by this plan is `20260709-06`.** It appears as a default in Phase 2 and as the publish target in Task 11. It is one string; do not let the two drift.
- **Python's `xml.etree.ElementTree` is broken on this machine** (`pyexpat` symbol mismatch). Parse Gradle JUnit XML with `grep`/`sed`.
- **Secret rotation is the user's task**, not part of any phase. Tasks 4 and 5 are blocked until the Cloudflare token is rotated.

---

## File Structure

**Phase 1 — package identity**

| File | Responsibility | Change |
|---|---|---|
| `app/android/android.gradle.kts` | Android app build config | `applicationId` |
| `app/desktop/desktop.gradle.kts` | Desktop packaging | `bundleID`, installer `packageName` |
| `app/android/google-services.json` | Firebase app registration | regenerated |
| `docs/HANDOFF.md` | operator notes | `adb` launch command |

**Phase 2 — environment split**

| File | Responsibility | Change |
|---|---|---|
| `shared/data/remoteconfig-api/.../RemoteConfigKey.kt` | RC key names | add `CatalogVersionStaging` |
| `shared/data/remoteconfig-api/.../RemoteConfigEntry.kt` | RC key + default pairing | add entry + `when` branch |
| `shared/data/remoteconfig-api/.../RemoteConfigDataDefaultsProvider.kt` | compiled-in defaults | add staging default; kill `99999999` |
| `shared/data/remoteconfig-api/.../RemoteConfigData.kt` | RC read surface | add `catalogVersionStaging` flow |
| `shared/data/remoteconfig-api/.../RemoteConfigDataMock.kt` | test double | implement new flow |
| `shared/data/remoteconfig-firebase/.../RemoteConfigDataDefault.kt` | live RC impl | implement new flow |
| `shared/core/di/.../NamedScope.kt` | Koin qualifiers | add `CatalogVersion` |
| `shared/di/di-base/.../ContentModule.kt` | shared bindings | **remove** env-specific bindings |
| `shared/di/di-buildconfig-release/.../BuildConfigModule.kt` | release-only bindings | prod URL + prod RC key |
| `shared/di/di-buildconfig-debug/.../BuildConfigModule.kt` | debug-only bindings | staging URL + staging RC key |
| `firebase-backend/remoteconfig.template.json` | RC server params | add `catalog_version_staging` |

**Phase 3 — media-map contract**

| File | Responsibility | Change |
|---|---|---|
| `shared/data/base/.../image/sized/MediaEntityKind.kt` | **the contract, stated once** | new |
| `shared/data/base/.../image/sized/SizedImage.kt` | wire-key ↔ enum | `from` → `fromOrNull` |
| `shared/data/mediamap/.../MediaMapMapper.kt` | wire → domain ingestion | drop unknown keys loudly |
| `service/content-pipeline/.../build/MediaMapBuilder.kt` | emits the map | derive from `MediaEntityKind` |
| `service/content-pipeline/.../validate/CatalogValidator.kt` | pre-publish gate | replace hardcoded key lists |
| `service/content-pipeline/.../Main.kt` | CLI entry | add `--dry-run` |

---

# Phase 1 — Package identity

### Task 1: Rename the application identity to `app.stillscenes`

`applicationId` is permanent once published. `namespace` is already `wallapp.app.android`, so **no Kotlin source moves**. `GoogleSignInFactory.kt:13` holds a project-scoped **web** OAuth client id (`client_type: 3`) — leave it alone.

**Files:**
- Modify: `app/android/android.gradle.kts:61`
- Modify: `app/desktop/desktop.gradle.kts:53,55`
- Modify: `app/android/google-services.json` (regenerated, not hand-edited)
- Modify: `docs/HANDOFF.md:59`

**Interfaces:**
- Consumes: nothing.
- Produces: `applicationId = "app.stillscenes"`. Task 11's `adb` command and all future launch commands use it. `NamedScope.ApplicationId` resolves to it automatically via `Factory.android.kt:599` (`context.packageName`).

- [ ] **Step 1: Read the debug keystore's SHA-1 — you need it before touching Firebase**

```bash
keytool -list -v -keystore app/android/debug.keystore \
        -alias androiddebugkey -storepass android -keypass android | grep 'SHA1:'
```

Expected: one line, `SHA1: XX:XX:...` (20 hex pairs). Copy it.

- [ ] **Step 2: Change `applicationId` and watch the build fail — this is the failing test**

The `com.google.gms.google-services` plugin (applied at `android.gradle.kts:259`) cross-checks `applicationId` against `google-services.json`. Changing one without the other must fail. That check *is* our test; there is no unit test to write here.

In `app/android/android.gradle.kts`, line 61:

```kotlin
            applicationId = "app.stillscenes"
```

- [ ] **Step 3: Run the build to verify it fails**

Run: `./gradlew :app:android:assembleWallAppDebug`

Expected: FAIL with `No matching client found for package name 'app.stillscenes'`

If it *succeeds*, stop — it means `google-services.json` already contains the new package and something is out of sync with this plan.

- [ ] **Step 4: Register the new Android app in Firebase**

In project `stillscenes-prod` (`809386236419`), console → Project settings → Your apps → Add app → Android:
- Package name: `app.stillscenes`
- Debug signing certificate SHA-1: the value from Step 1

Then download the regenerated `google-services.json` and replace `app/android/google-services.json` wholesale. Do **not** hand-merge it.

Leave the old `com.example.wallapp` app registered. It costs nothing and it is your rollback.

- [ ] **Step 5: Verify the new file carries both the package and an Android OAuth client**

```bash
grep -c '"package_name": "app.stillscenes"' app/android/google-services.json
grep -A2 '"client_type": 1' app/android/google-services.json | grep -c 'app.stillscenes'
```

Expected: first command prints `2` (the `android_client_info` and the `oauth_client` entry). Second prints at least `1`.

If the second prints `0`, the SHA-1 did not register, and Google Sign-In will fail at runtime with `DEVELOPER_ERROR` (status 10). Fix it now, not later.

- [ ] **Step 6: Run the build to verify it passes**

Run: `./gradlew :app:android:assembleWallAppDebug`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Rename the desktop bundle and installer**

In `app/desktop/desktop.gradle.kts`, line 53 and line 55:

```kotlin
            packageName = "StillScenes"
            macOS {
                bundleID = "app.stillscenes.desktop"
            }
```

- [ ] **Step 8: Update the launch command in the handoff doc**

In `docs/HANDOFF.md`, line 59, replace the `adb` command with:

```bash
adb shell am start -n app.stillscenes/wallapp.activity.MainActivity
```

- [ ] **Step 9: Confirm no shared code regressed**

Run:
```bash
./gradlew :shared:app:app-adapter:desktopTest
```
Expected: `BUILD SUCCESSFUL`. Nothing in `shared/` was touched, so this is a smoke check, not a real gate.

- [ ] **Step 10: Verify Google Sign-In end to end — the only real gate**

```bash
./gradlew :app:android:installWallAppDebug
adb shell am start -n app.stillscenes/wallapp.activity.MainActivity
```

Complete a Google Sign-In in the running app. This is the only step that exercises the new SHA-1 and the new `client_type: 1` OAuth client. Failure presents as `DEVELOPER_ERROR` / status code 10.

If it fails: **do not** swap the client id in `GoogleSignInFactory.kt`. That file correctly holds the *web* client id. A status-10 failure means the SHA-1 is wrong or missing — go back to Step 4.

- [ ] **Step 11: Commit**

```bash
git add app/android/android.gradle.kts app/desktop/desktop.gradle.kts \
        app/android/google-services.json docs/HANDOFF.md
git commit -m "feat(identity): rename applicationId to app.stillscenes

namespace was already wallapp.app.android, so no Kotlin sources move.
GoogleSignInFactory holds a project-scoped web OAuth client id and is
untouched; only the client_type:1 Android client is newly minted, keyed
on the debug keystore SHA-1."
```

---

# Phase 2 — Environment split

### Task 2: Add the staging catalog-version key and retire the demo sentinel

`catalogVersion` currently defaults to `"99999999"`, the demo catalog. Every cold start fetches its 219 items before Remote Config activates, then discards them — the source of the ~537 `MediaMap entry missing` warnings per launch.

**Files:**
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigKey.kt:10`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigEntry.kt:31,147`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigDataDefaultsProvider.kt:14`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigData.kt:13`
- Modify: `shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/RemoteConfigDataMock.kt:19`
- Modify: `shared/data/remoteconfig-firebase/src/commonMain/kotlin/wallapp/remoteconfig/RemoteConfigDataDefault.kt:56`
- Test: `shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/RemoteEndpointsRemoteConfigTest.kt`

**Interfaces:**
- Consumes: nothing.
- Produces: `RemoteConfigKey.CatalogVersionStaging` (key `"catalog_version_staging"`), `RemoteConfigEntry.CatalogVersionStaging`, `RemoteConfigDataDefaultsProvider.catalogVersionStaging: String`, `RemoteConfigData.catalogVersionStaging: StateFlow<String>`. Task 3 binds the flow.

- [ ] **Step 1: Write the failing test**

Replace the `CatalogVersionConfigTest` class at the top of `shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/RemoteEndpointsRemoteConfigTest.kt` with:

```kotlin
class CatalogVersionConfigTest {

    @Test
    fun catalogVersionEntry_hasStableKeyAndDefault() {
        assertEquals("catalog_version", RemoteConfigEntry.CatalogVersion.key)
        assertEquals("20260709-06", RemoteConfigEntry.CatalogVersion.default)
    }

    @Test
    fun catalogVersionStagingEntry_hasStableKeyAndDefault() {
        assertEquals("catalog_version_staging", RemoteConfigEntry.CatalogVersionStaging.key)
        assertEquals("20260709-06", RemoteConfigEntry.CatalogVersionStaging.default)
    }

    @Test
    fun mockData_exposesBothCatalogVersionDefaults() {
        val data = RemoteConfigDataMock(RemoteConfigDataDefaultsProviderDefault)
        assertEquals("20260709-06", data.catalogVersion.value)
        assertEquals("20260709-06", data.catalogVersionStaging.value)
    }

    @Test
    fun defaultsArray_registersBothCatalogKeys() {
        val keys = RemoteConfigEntry.asDefaultsArray().map { it.first }
        assertTrue("catalog_version" in keys)
        assertTrue("catalog_version_staging" in keys)
    }
}
```

Add `import kotlin.test.assertTrue` to the file's imports.

Then, in the same file, update the two `RemoteEndpointsRemoteConfigTest` assertions that still hardcode `99999999` — they use a fake `MutableStateFlow("99999999")`, which is a *test input*, not a default. Leave those two alone. They pass unchanged.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :shared:app:app-adapter:desktopTest --tests '*CatalogVersionConfigTest*'`
Expected: FAIL to compile — `Unresolved reference: CatalogVersionStaging`

- [ ] **Step 3: Add the RC key**

In `RemoteConfigKey.kt`, immediately after `CatalogVersion("catalog_version"),`:

```kotlin
    CatalogVersionStaging("catalog_version_staging"),
```

- [ ] **Step 4: Add the RC entry and its `when` branch**

In `RemoteConfigEntry.kt`, immediately after the `CatalogVersion` data object:

```kotlin
    data object CatalogVersionStaging : RemoteConfigEntry<String>(
        RemoteConfigKey.CatalogVersionStaging.key,
        Provider.catalogVersionStaging,
    )
```

And in `toRemoteConfigEntry()`, immediately after the `RemoteConfigKey.CatalogVersion ->` line:

```kotlin
        RemoteConfigKey.CatalogVersionStaging -> RemoteConfigEntry.CatalogVersionStaging
```

The `when` is exhaustive over `RemoteConfigKey`, so omitting this line is a compile error, not a runtime surprise.

- [ ] **Step 5: Replace the demo sentinel with the real catalog in the defaults provider**

In `RemoteConfigDataDefaultsProvider.kt`, replace lines 14-15:

```kotlin
    // Both environments start on the same catalog: the first prod publish writes the same bytes
    // staging already serves. Pointing this at the 99999999 demo catalog made every cold start
    // fetch 219 items it then discarded — the source of the MediaMap-entry-missing warning spam.
    open val catalogVersion: String
        get() = "20260709-06"
    open val catalogVersionStaging: String
        get() = "20260709-06"
```

- [ ] **Step 6: Add the flow to the read surface, the mock, and the live impl**

In `RemoteConfigData.kt`, immediately after line 13 (`val catalogVersion: StateFlow<String>`):

```kotlin
    val catalogVersionStaging: StateFlow<String>
```

In `RemoteConfigDataMock.kt`, immediately after the `catalogVersion` override:

```kotlin
    override val catalogVersionStaging: StateFlow<String> =
        MutableStateFlow(provider.catalogVersionStaging)
```

In `RemoteConfigDataDefault.kt`, immediately after line 56 (`override val catalogVersion: StateFlow<String> = CatalogVersion.toStateFlow()`):

```kotlin
    override val catalogVersionStaging: StateFlow<String> = CatalogVersionStaging.toStateFlow()
```

Add `CatalogVersionStaging` to the same import that already brings in `CatalogVersion` at the top of `RemoteConfigDataDefault.kt`.

- [ ] **Step 7: Run test to verify it passes**

Run: `./gradlew :shared:app:app-adapter:desktopTest :shared:data:remoteconfig-api:desktopTest`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: Commit**

```bash
git add shared/data/remoteconfig-api/src/commonMain/kotlin/wallapp/remoteconfig/data/ \
        shared/data/remoteconfig-firebase/src/commonMain/kotlin/wallapp/remoteconfig/RemoteConfigDataDefault.kt \
        shared/app/app-adapter/src/commonTest/kotlin/wallapp/remoteapi/RemoteEndpointsRemoteConfigTest.kt
git commit -m "feat(config): add catalog_version_staging and retire the 99999999 sentinel

The in-app default pointed at the demo catalog, so every cold start
fetched 219 items before Remote Config activated, then threw them away."
```

---

### Task 3: Move the CDN host and catalog-version key into the build-config seam

`ContentDeliveryConfig` is bound in the shared `ContentModule`, hardcoding staging. `RemoteEndpointsRepositoryRemoteConfig` already takes a bare `StateFlow<String>` and has no idea which RC key fed it — so vary the *flow*, not the config object. Both environment facts then live in one place per build type.

Do **not** add a `catalogVersionKey` field to `ContentDeliveryConfig`: `shared/data/remoteendpoint` does not depend on `shared/data/remoteconfig-api`, and adding that edge to express a fact neither module needs is the wrong trade.

**Files:**
- Modify: `shared/core/di/src/commonMain/kotlin/wallapp/di/NamedScope.kt`
- Modify: `shared/di/di-base/src/commonMain/kotlin/wallapp/di/module/ContentModule.kt:129-130,170`
- Modify: `shared/di/di-buildconfig-release/src/commonMain/kotlin/wallapp/di/module/BuildConfigModule.kt`
- Modify: `shared/di/di-buildconfig-debug/src/commonMain/kotlin/wallapp/di/module/BuildConfigModule.kt`
- Modify: `shared/di/di-buildconfig-release/di-buildconfig-release.gradle.kts`
- Modify: `shared/di/di-buildconfig-debug/di-buildconfig-debug.gradle.kts`
- Create: `shared/di/di-buildconfig-release/src/commonTest/kotlin/wallapp/di/module/BuildConfigModuleReleaseTest.kt`
- Create: `shared/di/di-buildconfig-debug/src/commonTest/kotlin/wallapp/di/module/BuildConfigModuleDebugTest.kt`
- Modify: `shared/app/app-adapter/src/commonTest/kotlin/wallapp/di/DeliveryPathWiringTest.kt`

**Interfaces:**
- Consumes: `RemoteConfigData.catalogVersion` and `RemoteConfigData.catalogVersionStaging` (Task 2); `RemoteConfigDataMock(provider)`; `RemoteConfigDataDefaultsProvider` (open class, `open val catalogVersion`, `open val catalogVersionStaging`).
- Produces: `NamedScope.CatalogVersion: Qualifier`, and a `StateFlow<String>` bound under it in each build-config module. `ContentDeliveryConfig` is no longer bound by `ContentModule`.

- [ ] **Step 1: Write the failing tests**

Create `shared/di/di-buildconfig-release/src/commonTest/kotlin/wallapp/di/module/BuildConfigModuleReleaseTest.kt`:

```kotlin
package wallapp.di.module

import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import wallapp.di.NamedScope
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProvider
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteendpoint.ContentDeliveryConfig
import kotlin.test.Test
import kotlin.test.assertEquals

/** Distinct values so the test can tell which Remote Config key was actually read. */
private object DistinguishableProvider : RemoteConfigDataDefaultsProvider() {
    override val catalogVersion: String get() = "prod-catalog"
    override val catalogVersionStaging: String get() = "staging-catalog"
}

class BuildConfigModuleReleaseTest {

    private val koin = koinApplication {
        modules(
            module { single<RemoteConfigData> { RemoteConfigDataMock(DistinguishableProvider) } },
            BuildConfigModule,
        )
    }.koin

    @Test
    fun releaseBuildsPointAtTheProductionCdn() {
        assertEquals("https://media.stillscenes.app", koin.get<ContentDeliveryConfig>().baseUrl)
    }

    @Test
    fun releaseBuildsReadTheProductionCatalogVersionKey() {
        val version: StateFlow<String> = koin.get(NamedScope.CatalogVersion)
        assertEquals("prod-catalog", version.value)
    }
}
```

Create `shared/di/di-buildconfig-debug/src/commonTest/kotlin/wallapp/di/module/BuildConfigModuleDebugTest.kt` — the same shape, asserting the staging values. Repeated in full rather than referenced, because tasks get read out of order:

```kotlin
package wallapp.di.module

import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import wallapp.di.NamedScope
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProvider
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteendpoint.ContentDeliveryConfig
import kotlin.test.Test
import kotlin.test.assertEquals

/** Distinct values so the test can tell which Remote Config key was actually read. */
private object DistinguishableProvider : RemoteConfigDataDefaultsProvider() {
    override val catalogVersion: String get() = "prod-catalog"
    override val catalogVersionStaging: String get() = "staging-catalog"
}

class BuildConfigModuleDebugTest {

    private val koin = koinApplication {
        modules(
            module { single<RemoteConfigData> { RemoteConfigDataMock(DistinguishableProvider) } },
            BuildConfigModule,
        )
    }.koin

    @Test
    fun debugBuildsPointAtTheStagingCdn() {
        assertEquals("https://media-staging.stillscenes.app", koin.get<ContentDeliveryConfig>().baseUrl)
    }

    @Test
    fun debugBuildsReadTheStagingCatalogVersionKey() {
        val version: StateFlow<String> = koin.get(NamedScope.CatalogVersion)
        assertEquals("staging-catalog", version.value)
    }
}
```

The debug module's `BuildConfigModule` also binds `BillingManagerDebug` and `DebugManagerDebug`, which need a wider graph. Koin does not instantiate `single` definitions until they are requested, so resolving only `ContentDeliveryConfig` and the qualified `StateFlow` is safe. If `koinApplication {}` proves eager in practice, use `koinApplication { allowOverride(true); modules(...) }` and resolve lazily — do **not** call `checkModules()` here.

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :shared:di:di-buildconfig-release:desktopTest :shared:di:di-buildconfig-debug:desktopTest`
Expected: FAIL to compile — `Unresolved reference: CatalogVersion` (on `NamedScope`), and for the release module, `Unresolved reference: ContentDeliveryConfig`.

- [ ] **Step 3: Add the Koin qualifier**

In `shared/core/di/src/commonMain/kotlin/wallapp/di/NamedScope.kt`, in alphabetical position (immediately after `CacheFileMediaMap`):

```kotlin
    val CatalogVersion = named("CatalogVersion")
```

- [ ] **Step 4: Give the release module the dependencies it now needs**

In `shared/di/di-buildconfig-release/di-buildconfig-release.gradle.kts`, inside `commonMain { dependencies { ... } }`, add in alphabetical position:

```kotlin
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
```

And add a test source set to the same file's `sourceSets { }` block:

```kotlin
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.koin.core)
                implementation(project(":shared:data:remoteconfig-api"))
                implementation(project(":shared:data:remoteendpoint"))
            }
        }
```

In `shared/di/di-buildconfig-debug/di-buildconfig-debug.gradle.kts`, add the same `commonTest` block. Its `commonMain` already has both projects, so no `commonMain` change is needed there.

If the convention plugin (`wallapp.kotlin.multiplatform`) already supplies `kotlin("test")`, Gradle deduplicates and the explicit line is harmless.

- [ ] **Step 5: Bind the prod host and prod RC key in the release module**

Replace `shared/di/di-buildconfig-release/src/commonMain/kotlin/wallapp/di/module/BuildConfigModule.kt` entirely:

```kotlin
package wallapp.di.module

import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module
import wallapp.billing.BillingManager
import wallapp.billing.BillingManagerNoOp
import wallapp.content.state.debug.DebugManager
import wallapp.content.state.debug.DebugManagerNoOp
import wallapp.di.NamedScope
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteendpoint.ContentDeliveryConfig

@Suppress("RemoveExplicitTypeArguments")
val BuildConfigModule = module {
    single<BillingManager>(NamedScope.BillingManagerFallback) { BillingManagerNoOp() }
    single<ContentDeliveryConfig> { ContentDeliveryConfig(baseUrl = "https://media.stillscenes.app") }
    single<DebugManager> { DebugManagerNoOp }
    single<StateFlow<String>>(NamedScope.CatalogVersion) { get<RemoteConfigData>().catalogVersion }
}
```

- [ ] **Step 6: Bind the staging host and staging RC key in the debug module**

In `shared/di/di-buildconfig-debug/src/commonMain/kotlin/wallapp/di/module/BuildConfigModule.kt`, add these imports:

```kotlin
import kotlinx.coroutines.flow.StateFlow
import wallapp.remoteconfig.data.RemoteConfigData
import wallapp.remoteendpoint.ContentDeliveryConfig
```

and these two bindings inside the `module { }` block:

```kotlin
    single<ContentDeliveryConfig> { ContentDeliveryConfig(baseUrl = "https://media-staging.stillscenes.app") }
    single<StateFlow<String>>(NamedScope.CatalogVersion) { get<RemoteConfigData>().catalogVersionStaging }
```

- [ ] **Step 7: Strip the environment out of the shared module**

In `shared/di/di-base/src/commonMain/kotlin/wallapp/di/module/ContentModule.kt`, delete lines 129-130 entirely:

```kotlin
    // TODO(release plan): staging-only base URL; prod/staging split lands with per-buildtype config.
    single<ContentDeliveryConfig> { ContentDeliveryConfig(baseUrl = "https://media-staging.stillscenes.app") }
```

Then change line 170 from:

```kotlin
    single<RemoteEndpointsRepositoryNetwork> { RemoteEndpointsRepositoryRemoteConfig(catalogVersion = get<RemoteConfigData>().catalogVersion, config = get()) }
```

to:

```kotlin
    single<RemoteEndpointsRepositoryNetwork> { RemoteEndpointsRepositoryRemoteConfig(catalogVersion = get(NamedScope.CatalogVersion), config = get()) }
```

Remove the now-unused `import wallapp.remoteendpoint.ContentDeliveryConfig`. Leave the `RemoteConfigData` import if any other binding still uses it; if the compiler flags it as unused, remove it too.

- [ ] **Step 8: Run the new tests to verify they pass**

Run: `./gradlew :shared:di:di-buildconfig-release:desktopTest :shared:di:di-buildconfig-debug:desktopTest`
Expected: `BUILD SUCCESSFUL`, 4 tests.

- [ ] **Step 9: Fix the app-adapter test, which pinned a constant instead of a behavior**

`app-adapter:desktopTest` links `di-buildconfig-debug` (`di-base.gradle.kts:141`, *"Desktop always uses Debug for now"*), so it can only ever observe the staging binding. Say so.

Replace the body of `shared/app/app-adapter/src/commonTest/kotlin/wallapp/di/DeliveryPathWiringTest.kt`:

```kotlin
class DeliveryPathWiringTest : WaeTest {

    // Desktop links di-buildconfig-debug (see di-base.gradle.kts). This asserts the graph resolves
    // to staging rather than prod; the prod binding is asserted in di-buildconfig-release's own test,
    // which is the only place it is reachable.
    @Test fun debugGraphResolvesToTheStagingCdn() = waeTest {
        val config: ContentDeliveryConfig = resolveDependency()
        assertEquals("https://media-staging.stillscenes.app", config.baseUrl)
    }

    @Test fun remoteEndpointsAreBuiltFromRemoteConfigCatalogVersion() = waeTest {
        val repository: RemoteEndpointsRepositoryNetwork = resolveDependency()
        assertIs<RemoteEndpointsRepositoryRemoteConfig>(repository)
        repository.getRemoteEndpoints(RemoteEndpointTrack.Production).test {
            val endpoints = awaitItem()!!
            assertEquals("https://media-staging.stillscenes.app/api/20260709-06/content-1a", endpoints.content)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

- [ ] **Step 10: Run the full app test suite**

Run:
```bash
./gradlew :shared:app:app-adapter:desktopTest :shared:domain:content-state:desktopTest \
          :shared:data:content:desktopTest :shared:data:remoteconfig-api:desktopTest \
          :shared:data:remoteapi:desktopTest
```
Expected: `BUILD SUCCESSFUL`.

If `DeliveryPathWiringTest` fails to resolve `ContentDeliveryConfig`, check `shared/test/test-common/.../WaeTestModule.kt` — it is known to diverge from production wiring (it binds `NetworkMediaMapRepository` to the old `NetworkMediaMapRepositoryNetwork`). It may need `BuildConfigModule` added to its module list.

- [ ] **Step 11: Commit**

```bash
git add shared/core/di/src/commonMain/kotlin/wallapp/di/NamedScope.kt \
        shared/di/di-base/src/commonMain/kotlin/wallapp/di/module/ContentModule.kt \
        shared/di/di-buildconfig-release/ shared/di/di-buildconfig-debug/ \
        shared/app/app-adapter/src/commonTest/kotlin/wallapp/di/DeliveryPathWiringTest.kt
git commit -m "feat(delivery): resolve CDN host and catalog key per build type

ContentModule no longer names a host or an RC key. RemoteEndpointsRepositoryRemoteConfig
already took a bare StateFlow<String>, so the flow varies rather than the config object,
and remoteendpoint keeps its independence from remoteconfig-api."
```

---

### Task 4: Deploy the staging Remote Config key

> **BLOCKED** on the user rotating the Cloudflare token and the BFL API key. This task itself needs only Firebase, but Task 5 needs the token and both must land before any release build.

**Files:**
- Modify: `firebase-backend/remoteconfig.template.json`

**Interfaces:**
- Consumes: the key name `catalog_version_staging` from Task 2.
- Produces: a server-side RC parameter. Until it exists, clients silently fall back to the compiled-in default — which Task 2 already set to the correct value, so the fallback is harmless. Deploy anyway; it will not always be harmless.

- [ ] **Step 1: Add the parameter, immediately after `catalog_version`**

```json
    "catalog_version_staging": {
      "defaultValue": {
        "value": "20260709-06"
      },
      "description": "Content catalog version pointer for debug builds, which read media-staging.stillscenes.app. Release builds read catalog_version instead. Flip this to preview a catalog before prod can serve it.",
      "valueType": "STRING"
    },
```

- [ ] **Step 2: Verify the template is valid JSON before deploying**

```bash
python3 -c "import json;json.load(open('firebase-backend/remoteconfig.template.json'));print('ok')"
```
Expected: `ok`

(`json` is fine on this machine. Only `xml.etree` is broken.)

- [ ] **Step 3: Deploy**

```bash
firebase deploy --only remoteconfig -P stillscenes-prod
```
Expected: `Deploy complete!`

The auto-mode classifier gates prod RC deploys. It requires the specific action named — "deploy catalog_version_staging to stillscenes-prod Remote Config". A bare "go" does not clear it.

- [ ] **Step 4: Verify the server actually has it**

```bash
firebase remoteconfig:get -P stillscenes-prod | grep -A3 catalog_version_staging
```
Expected: the parameter, with value `20260709-06`.

Do **not** verify this by reading app logs. Remote Config silently serves in-app defaults for keys the server does not define, so a log line showing the right value proves nothing.

- [ ] **Step 5: Commit**

```bash
git add firebase-backend/remoteconfig.template.json
git commit -m "feat(config): add catalog_version_staging Remote Config parameter"
```

---

### Task 5: Stand up the prod CDN hostname

> **BLOCKED** on the rotated Cloudflare token. No repo files change; this is infrastructure. Record what you did in `docs/HANDOFF.md`.

**Files:**
- Modify: `docs/HANDOFF.md` (record the new hostname and cache rule)

**Interfaces:**
- Consumes: `applicationId` is irrelevant here; the bucket `stillscenes-content-prod` already exists and is empty.
- Produces: `https://media.stillscenes.app` serving `stillscenes-content-prod`. Task 3's release binding already points at it. Task 11 publishes into it.

- [ ] **Step 1: Check whether the custom domain is already connected — it probably is**

```bash
curl -sSI https://media.stillscenes.app/api/20260709-06/content-1a | grep -iE '^HTTP|^server|^cf-ray'
```

Observed on 2026-07-10: `HTTP/2 404`, `server: cloudflare`, a `cf-ray` header. A 404 *from Cloudflare* — as
opposed to a DNS failure — means the hostname already resolves and routes to an R2 bucket that is empty.
The custom domain was very likely attached during the 2026-07-07 infra work, alongside `media-staging`.

If that is what you see, **skip to Step 3.** Only if DNS fails outright:

Cloudflare dashboard → R2 → `stillscenes-content-prod` → Settings → Custom Domains → Connect Domain → `media.stillscenes.app`.

Cloudflare adds the CNAME automatically. **Connecting a custom domain makes the bucket publicly readable.** That is intended for a CDN origin, and the bucket is empty right now — which is exactly why this step comes before the publish.

- [ ] **Step 2: Verify DNS and that the bucket is reachable and empty**

```bash
dig +short media.stillscenes.app
curl -sS -o /dev/null -w '%{http_code}\n' https://media.stillscenes.app/api/20260709-06/content-1a
```
Expected: `dig` prints a Cloudflare address. `curl` prints `404` — the domain resolves and the bucket is empty. A `000` or `5xx` means the domain is not wired.

- [ ] **Step 3: Replicate the extensionless-`/api/` cache rule**

Copy the existing rule from the `media-staging.stillscenes.app` hostname, changing only the hostname. This is **required, not an optimization**: Cloudflare does not cache JSON or HTML by default, and our catalog objects are extensionless (`/api/<version>/content-1a`). Without the rule every request is a cache miss that bills an R2 Class B operation.

- [ ] **Step 4: Record it in the handoff**

In `docs/HANDOFF.md`, update the "Current production state" table so the CDN row reads:

```markdown
| CDN (release) | `media.stillscenes.app` → R2 `stillscenes-content-prod` |
| CDN (debug) | `media-staging.stillscenes.app` → R2 `stillscenes-content-staging` |
```

- [ ] **Step 5: Commit**

```bash
git add docs/HANDOFF.md
git commit -m "docs: record the prod CDN hostname and its cache rule"
```

---

# Phase 3 — Media-map contract

### Task 6: State the media-map contract once

The required keys are currently written out three times: in `MediaMapBuilder`'s string literals, in `CatalogValidator:19,23`'s hardcoded `listOf(...)`, and implicitly in whatever the renderers ask for. They drifted, twice.

**Files:**
- Create: `shared/data/base/src/commonMain/kotlin/wallapp/image/sized/MediaEntityKind.kt`
- Create: `shared/data/base/src/commonTest/kotlin/wallapp/image/sized/MediaEntityKindTest.kt`
- Modify: `shared/data/base/base.gradle.kts`

**Interfaces:**
- Consumes: `SizedImage` (existing enum, `shared/data/base`, property `key: String`).
- Produces: `MediaEntityKind` enum with `requiredKeys: Set<SizedImage>` and `requiredKeyStrings: Set<String>`. Tasks 8 and 9 both consume it.

- [ ] **Step 1: Add a test source set**

In `shared/data/base/base.gradle.kts`, inside `sourceSets { }`, immediately after the `commonMain { }` block:

```kotlin
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
```

- [ ] **Step 2: Write the failing test**

Create `shared/data/base/src/commonTest/kotlin/wallapp/image/sized/MediaEntityKindTest.kt`:

```kotlin
package wallapp.image.sized

import kotlin.test.Test
import kotlin.test.assertEquals

class MediaEntityKindTest {

    /**
     * A SizedImage that no media-map entry carries is a renderer asking for something the pipeline
     * never emits. This is what let wcs0..wcl2 sit unemitted while collection cards rendered broken.
     */
    @Test
    fun everySizedImageIsCarriedBySomeEntityKind() {
        val emitted = MediaEntityKind.entries.flatMap { it.requiredKeys }.toSet()
        assertEquals(
            emptySet(),
            SizedImage.entries.toSet() - emitted,
            "SizedImage values that no MediaEntityKind emits",
        )
    }

    @Test
    fun sizedImageKeysAreUnique() {
        assertEquals(
            SizedImage.entries.size,
            SizedImage.entries.map { it.key }.distinct().size,
            "duplicate wire keys in SizedImage",
        )
    }

    @Test
    fun requiredKeyStringsMatchTheEnumKeys() {
        assertEquals(setOf("dhd", "dsd"), MediaEntityKind.WallpaperDownload.requiredKeyStrings)
        assertEquals(setOf("e"), MediaEntityKind.FolderBanner.requiredKeyStrings)
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `./gradlew :shared:data:base:desktopTest`
Expected: FAIL to compile — `Unresolved reference: MediaEntityKind`

- [ ] **Step 4: Write the enum**

Create `shared/data/base/src/commonMain/kotlin/wallapp/image/sized/MediaEntityKind.kt`:

```kotlin
package wallapp.image.sized

/**
 * Which [SizedImage] keys each kind of media-map entry must carry.
 *
 * The media map is a `mediaId -> { SizedImage.key -> url }` wire structure with no schema. Before
 * this enum the required keys were written out in three places — the pipeline's MediaMapBuilder,
 * CatalogValidator, and implicitly in whatever the renderers requested — and they drifted twice.
 * State the fact once, here.
 *
 * Declaration order within each set is the wire key order, and the pipeline's JSON preserves it.
 * Reordering a set changes published bytes.
 */
enum class MediaEntityKind(val requiredKeys: Set<SizedImage>) {

    WallpaperDownload(
        setOf(
            SizedImage.DownloadableWallpaperHd,
            SizedImage.DownloadableWallpaperSd,
        ),
    ),

    /** A collection card stacks three preview layers, each looked up by its own key. */
    WallpaperPreview(
        setOf(
            SizedImage.Showcase,
            SizedImage.WallpaperFeedSingle,
            SizedImage.WallpaperFeedTrack,
            SizedImage.FullScreen,
            SizedImage.WallpaperCollectionSmallLayer0,
            SizedImage.WallpaperCollectionSmallLayer1,
            SizedImage.WallpaperCollectionSmallLayer2,
            SizedImage.WallpaperCollectionLargeLayer0,
            SizedImage.WallpaperCollectionLargeLayer1,
            SizedImage.WallpaperCollectionLargeLayer2,
        ),
    ),

    ArtistProfile(
        setOf(
            SizedImage.ArtistMedium,
            SizedImage.ArtistSmall,
            SizedImage.Exhibit,
        ),
    ),

    FolderProfile(setOf(SizedImage.WallpaperFeedSingle)),

    /** The carousel highlight reads Exhibit. A feed key here leaves the card with no background. */
    FolderBanner(setOf(SizedImage.Exhibit)),

    ;

    val requiredKeyStrings: Set<String> get() = requiredKeys.mapTo(LinkedHashSet()) { it.key }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `./gradlew :shared:data:base:desktopTest`
Expected: `BUILD SUCCESSFUL`, 3 tests.

If `everySizedImageIsCarriedBySomeEntityKind` fails, a `SizedImage` was added without deciding which entity carries it. That is the bug the test exists to find — do not delete the assertion.

- [ ] **Step 6: Commit**

```bash
git add shared/data/base/base.gradle.kts \
        shared/data/base/src/commonMain/kotlin/wallapp/image/sized/MediaEntityKind.kt \
        shared/data/base/src/commonTest/kotlin/wallapp/image/sized/MediaEntityKindTest.kt
git commit -m "feat(media): declare the media-map key contract in one place"
```

---

### Task 7: Stop unknown wire keys from silently clobbering the feed image

`SizedImage.from()` ends in `?: Preset`. `MediaMapMapper.kt:15` feeds every wire key through it, so a typo'd key such as `"wsc0"` resolves to `WallpaperFeedSingle` and **overwrites the real `wfs` entry** — corrupting the feed image for every wallpaper, with no warning anywhere. `MediaMapMapper` is the only caller.

The `require(...)` duplicate-key check inside `from()` currently runs on every single call. It belongs in a test, and Task 6 already put it there.

**Files:**
- Modify: `shared/data/base/src/commonMain/kotlin/wallapp/image/sized/SizedImage.kt:44-54`
- Modify: `shared/data/mediamap/src/commonMain/kotlin/wallapp/mediamap/MediaMapMapper.kt`
- Create: `shared/data/mediamap/src/commonTest/kotlin/wallapp/mediamap/MediaMapMapperTest.kt`
- Modify: `shared/data/mediamap/mediamap.gradle.kts`

**Interfaces:**
- Consumes: `SizedImage.entries`, `SizedImage.key`.
- Produces: `SizedImage.fromOrNull(key: String): SizedImage?`. `SizedImage.from` is **removed**. Task 9's validator calls `fromOrNull`.

- [ ] **Step 1: Add a test source set to the mediamap module**

In `shared/data/mediamap/mediamap.gradle.kts`, inside `sourceSets { }`, after `commonMain { }`:

```kotlin
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
```

- [ ] **Step 2: Write the failing test**

Create `shared/data/mediamap/src/commonTest/kotlin/wallapp/mediamap/MediaMapMapperTest.kt`:

```kotlin
package wallapp.mediamap

import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MediaMapMapperTest {

    @Test
    fun knownKeysMapToTheirSizedImage() {
        val result = MediaMapMapper.mapToMediaMap(
            mapOf(1L to mapOf("wfs" to "https://cdn/real.webp", "e" to "https://cdn/banner.webp")),
        )
        val entry = result[MediaId(1L)]!!
        assertEquals("https://cdn/real.webp", entry[SizedImage.WallpaperFeedSingle]!!.url)
        assertEquals("https://cdn/banner.webp", entry[SizedImage.Exhibit]!!.url)
    }

    /**
     * Before fromOrNull, an unknown key resolved to Preset (== WallpaperFeedSingle) and overwrote
     * the real wfs entry. A single pipeline typo would have blanked the feed for every wallpaper.
     */
    @Test
    fun unknownKeyIsDroppedAndDoesNotClobberThePresetEntry() {
        val result = MediaMapMapper.mapToMediaMap(
            mapOf(1L to mapOf("wfs" to "https://cdn/real.webp", "wsc0" to "https://cdn/typo.webp")),
        )
        val entry = result[MediaId(1L)]!!
        assertEquals("https://cdn/real.webp", entry[SizedImage.WallpaperFeedSingle]!!.url)
        assertEquals(1, entry.size)
    }

    @Test
    fun fromOrNullRejectsUnknownKeys() {
        assertEquals(SizedImage.Exhibit, SizedImage.fromOrNull("e"))
        assertNull(SizedImage.fromOrNull("wsc0"))
    }
}
```

If `ImageModel` does not expose a `url` property, assert on `entry[SizedImage.WallpaperFeedSingle]` equality against `ImageModel.from("https://cdn/real.webp")` instead — `ImageModel` is a data class in `shared/data/base`.

- [ ] **Step 3: Run test to verify it fails**

Run: `./gradlew :shared:data:mediamap:desktopTest`
Expected: FAIL — `unknownKeyIsDroppedAndDoesNotClobberThePresetEntry` reports `expected:<https://cdn/real.webp> but was:<https://cdn/typo.webp>`, and `fromOrNullRejectsUnknownKeys` fails to compile.

That failure message *is* the bug, reproduced.

- [ ] **Step 4: Make the lookup total**

Replace the companion object in `shared/data/base/src/commonMain/kotlin/wallapp/image/sized/SizedImage.kt`:

```kotlin
    companion object {
        val Preset = WallpaperFeedSingle

        /**
         * Returns null for a key this build does not know. Callers must decide what that means;
         * silently substituting [Preset] once let a typo'd wire key overwrite a real entry.
         * Key uniqueness is asserted in MediaEntityKindTest, not re-checked on every call.
         */
        fun fromOrNull(key: String): SizedImage? = entries.firstOrNull { it.key == key }
    }
```

- [ ] **Step 5: Drop unknown keys, loudly**

Replace `shared/data/mediamap/src/commonMain/kotlin/wallapp/mediamap/MediaMapMapper.kt`:

```kotlin
package wallapp.mediamap

import wallapp.image.ImageModel
import wallapp.image.sized.SizedImage
import wallapp.media.model.MediaId
import wallapp.media.network.model.NetworkMediaMap

object MediaMapMapper {

    private val Log = MediaMapLogger

    fun mapToMediaMap(networkMediaMaps: Map<Long, NetworkMediaMap>): Map<MediaId, MediaMap> {
        return mutableMapOf<MediaId, MutableMap<SizedImage, ImageModel>>().apply {
            networkMediaMaps.forEach { (mediaId, networkMediaMap) ->
                val mediaMap = mutableMapOf<SizedImage, ImageModel>()
                networkMediaMap.forEach { (sizedImage, url) ->
                    val known = SizedImage.fromOrNull(sizedImage)
                    if (known == null) {
                        Log.w("Unknown SizedImage key $sizedImage on mediaId $mediaId; entry ignored")
                    } else {
                        mediaMap[known] = ImageModel.from(url)
                    }
                }

                put(MediaId(mediaId), mediaMap)
            }
        }
    }
}
```

`MediaMapLogger` is a top-level `val` in this same package (`MediaMapLogger.kt`), so no import is needed. The codebase's convention is `val Log = MediaMapLogger` inside a companion; this is an `object`, so a private property is the equivalent.

- [ ] **Step 6: Run tests to verify they pass**

Run: `./gradlew :shared:data:mediamap:desktopTest :shared:data:base:desktopTest`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Confirm nothing else called the removed function**

```bash
grep -rn "SizedImage.from(" --include="*.kt" . | grep -v node_modules | grep -v '/build/'
```
Expected: no output. `MediaMapMapper` was the only caller.

- [ ] **Step 8: Run the full app suite**

Run:
```bash
./gradlew :shared:app:app-adapter:desktopTest :shared:domain:content-state:desktopTest \
          :shared:data:content:desktopTest :shared:data:mediamap:desktopTest
```
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 9: Commit**

```bash
git add shared/data/base/src/commonMain/kotlin/wallapp/image/sized/SizedImage.kt \
        shared/data/mediamap/mediamap.gradle.kts \
        shared/data/mediamap/src/commonMain/kotlin/wallapp/mediamap/MediaMapMapper.kt \
        shared/data/mediamap/src/commonTest/kotlin/wallapp/mediamap/MediaMapMapperTest.kt
git commit -m "fix(media): an unknown wire key must not alias onto the Preset entry

SizedImage.from() fell back to Preset, so a typo'd key such as wsc0 resolved
to WallpaperFeedSingle and overwrote the real wfs url for every wallpaper."
```

---

### Task 8: Derive the built media map from the contract

`MediaMapBuilder` writes the keys as string literals. Make it read them from `MediaEntityKind` instead. The published bytes must not change — key order in the JSON follows map iteration order, and `setOf(...)` preserves declaration order, so `MediaEntityKind`'s declaration order was chosen to match the current output exactly.

**Files:**
- Modify: `service/content-pipeline/content-pipeline.gradle.kts`
- Modify: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/MediaMapBuilder.kt`
- Modify: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/MediaMapBuilderTest.kt`

**Interfaces:**
- Consumes: `MediaEntityKind.requiredKeyStrings` (Task 6); `mediaId(seed: String): Long` and `renditionUrl(baseUrl: String, path: String): String` from `MediaIds.kt`; `PipelineManifest`.
- Produces: `MediaMapBuilder.build(m: PipelineManifest): NetworkMediaData`, unchanged signature and unchanged output bytes.

- [ ] **Step 1: Let the pipeline see the contract**

In `service/content-pipeline/content-pipeline.gradle.kts`, in `dependencies { }`, add in alphabetical position among the `project(...)` lines:

```kotlin
    implementation(project(":shared:data:base"))
```

This edge is safe: the pipeline is `kotlin("jvm")` and already depends on `:shared:data:mediamap-network` and `:shared:data:content-network`, which declare the identical `androidTarget()/desktop()/iOS()` target triple as `:shared:data:base`.

- [ ] **Step 2: Write the characterization test**

This is a refactor, so the test pins current behaviour rather than driving new behaviour. It must pass **before** and **after**. Add to `service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/MediaMapBuilderTest.kt`:

```kotlin
    /**
     * JSON key order follows map iteration order, so a reordering here changes published bytes.
     * Pinned so the MediaEntityKind refactor is provably byte-identical.
     */
    @Test fun previewEntryPreservesWireKeyOrder() {
        val pv = MediaMapBuilder.build(manifest()).mediaMap[mediaId("stillscenes_1a2b3c4d:preview")]!!
        assertEquals(
            listOf("s", "wfs", "wft", "fs", "wcs0", "wcs1", "wcs2", "wcl0", "wcl1", "wcl2"),
            pv.keys.toList(),
        )
    }

    @Test fun downloadAndArtistEntriesPreserveWireKeyOrder() {
        val data = MediaMapBuilder.build(manifest())
        assertEquals(
            listOf("dhd", "dsd"),
            data.mediaMap[mediaId("stillscenes_1a2b3c4d:download")]!!.keys.toList(),
        )
        assertEquals(
            listOf("am", "as", "e"),
            data.mediaMap[mediaId("stillscenes:profile")]!!.keys.toList(),
        )
    }
```

- [ ] **Step 3: Run it against the CURRENT implementation to verify it passes**

Run: `./gradlew :service:content-pipeline:test --tests '*MediaMapBuilderTest*'`
Expected: PASS, 8 tests.

If it fails now, the declaration order in `MediaEntityKind` (Task 6) is wrong and must be corrected to match reality before proceeding. Do not "fix" this by editing the expectation.

- [ ] **Step 4: Rewrite the builder to derive from the contract**

Replace `service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/MediaMapBuilder.kt`:

```kotlin
package wallapp.pipeline.build

import wallapp.image.sized.MediaEntityKind
import wallapp.media.network.model.NetworkMediaData
import wallapp.pipeline.manifest.PipelineManifest

object MediaMapBuilder {

    fun build(m: PipelineManifest): NetworkMediaData {
        val data = LinkedHashMap<Long, Map<String, String>>()
        fun url(path: String) = renditionUrl(m.baseUrl, path)
        fun entry(kind: MediaEntityKind, url: String): Map<String, String> =
            kind.requiredKeyStrings.associateWithTo(LinkedHashMap()) { url }

        for (w in m.wallpapers) {
            data[mediaId("${w.id}:download")] =
                entry(MediaEntityKind.WallpaperDownload, url(w.downloadRenditionPath))
            data[mediaId("${w.id}:preview")] =
                entry(MediaEntityKind.WallpaperPreview, url(w.previewRenditionPath))
        }
        data[mediaId("${m.artist.id}:profile")] =
            entry(MediaEntityKind.ArtistProfile, url(m.artist.profileImagePath))
        data[mediaId("${m.folder.id}:profile")] =
            entry(MediaEntityKind.FolderProfile, url(m.folder.profileImagePath))
        data[mediaId("${m.folder.id}:banner")] =
            entry(MediaEntityKind.FolderBanner, url(m.folder.featureBannerImagePath))

        return NetworkMediaData(version = 1, mediaMap = data)
    }
}
```

The private `CollectionLayerKeys` list is deleted. So is every string literal.

- [ ] **Step 5: Run the tests to verify they still pass**

Run: `./gradlew :service:content-pipeline:test --tests '*MediaMapBuilderTest*'`
Expected: PASS, 8 tests — the same 8, with identical output.

- [ ] **Step 6: Commit**

```bash
git add service/content-pipeline/content-pipeline.gradle.kts \
        service/content-pipeline/src/main/kotlin/wallapp/pipeline/build/MediaMapBuilder.kt \
        service/content-pipeline/src/test/kotlin/wallapp/pipeline/build/MediaMapBuilderTest.kt
git commit -m "refactor(pipeline): derive the media map from MediaEntityKind

Byte-identical output, pinned by a wire-key-order characterization test.
The builder no longer contains a single SizedImage string literal."
```

---

### Task 9: Make the validator check the contract instead of a stale copy of it

`CatalogValidator:19` and `:23` hardcode `listOf("dhd","dsd")` and `listOf("s","wfs")`. That list is the *third* copy of the contract, and it is incomplete — which is exactly why the validator caught neither `c2db6cf` nor `fa8453e`.

Note on honesty: once the builder derives from `MediaEntityKind`, the validator's per-kind key check is a **tripwire**, not an independent verification — it confirms the enum equals itself under the current builder. It still earns its place: it catches a hand-edited catalog, an older builder's output, and any future regression in derivation.

**Corrected after review:** this step originally claimed the unknown-key scan and the orphan/collision check were both "genuinely independent." Only the orphan/collision check is. The unknown-key scan is subsumed — a bundle that passes the per-kind and orphan checks cannot fail it, because every member of a `requiredKeyStrings` is a `SizedImage.key` by construction. It runs **first** regardless, because `unknown SizedImage key 'wsc0'` diagnoses the fault far better than a set-inequality dump, and it is the check that still works if the per-kind check is ever weakened. Order in the shipped code is: (a1) unknown-key scan, (a2) per-kind sets, (a3) orphan/collision.

**Files:**
- Modify: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/validate/CatalogValidator.kt`
- Modify: `service/content-pipeline/src/test/kotlin/wallapp/pipeline/validate/CatalogValidatorTest.kt`

**Interfaces:**
- Consumes: `MediaEntityKind.requiredKeyStrings` (Task 6); `SizedImage.fromOrNull` (Task 7); `WireBundle(content, search, media, baseUrl, imgixHostPrefix)`.
- Produces: `CatalogValidator.validate(b: WireBundle)`, unchanged signature, stricter behaviour.

- [ ] **Step 1: Write the failing tests**

Append to `service/content-pipeline/src/test/kotlin/wallapp/pipeline/validate/CatalogValidatorTest.kt`. Build a valid `WireBundle` using whatever helper that file already uses, then corrupt one field per test:

```kotlin
    @Test fun rejectsAFolderBannerCarryingTheFeedKeyInsteadOfExhibit() {
        val bundle = validBundle()
        val bannerId = bundle.content.folders.first().featureBannerImage.id
        val broken = bundle.copy(
            media = bundle.media.copy(
                mediaMap = bundle.media.mediaMap.toMutableMap().apply {
                    put(bannerId, mapOf("wfs" to "https://cdn/banner.webp"))
                },
            ),
        )
        val e = assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
        assertTrue(e.message!!.contains("folder banner"))
    }

    @Test fun rejectsAnUnknownSizedImageKey() {
        val bundle = validBundle()
        val previewId = bundle.content.wallpapers.first().previews.standard.first().id
        val broken = bundle.copy(
            media = bundle.media.copy(
                mediaMap = bundle.media.mediaMap.toMutableMap().apply {
                    put(previewId, bundle.media.mediaMap[previewId]!! + ("wsc0" to "https://cdn/typo.webp"))
                },
            ),
        )
        val e = assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
        assertTrue(e.message!!.contains("wsc0"))
    }

    @Test fun rejectsAnOrphanMediaMapEntry() {
        val bundle = validBundle()
        val broken = bundle.copy(
            media = bundle.media.copy(
                mediaMap = bundle.media.mediaMap + (999_999_999L to mapOf("wfs" to "https://cdn/x.webp")),
            ),
        )
        val e = assertFailsWith<IllegalStateException> { CatalogValidator.validate(broken) }
        assertTrue(e.message!!.contains("orphan"))
    }

    @Test fun acceptsAWellFormedBundle() {
        CatalogValidator.validate(validBundle())
    }
```

Add `import kotlin.test.assertFailsWith` and `import kotlin.test.assertTrue`. If `CatalogValidatorTest.kt` has no `validBundle()` helper, write one that returns a `WireBundle` built from `MediaMapBuilder.build(manifest())` plus the matching `CatalogBuilder` and `SearchBuilder` output for the same manifest — that keeps content ids and media ids consistent, which every check below depends on.

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :service:content-pipeline:test --tests '*CatalogValidatorTest*'`
Expected: FAIL — `rejectsAFolderBannerCarryingTheFeedKeyInsteadOfExhibit` and `rejectsAnOrphanMediaMapEntry` do not throw; `rejectsAnUnknownSizedImageKey` does not throw.

- [ ] **Step 3: Replace the hardcoded key lists and add the independent checks**

In `CatalogValidator.kt`, add imports:

```kotlin
import wallapp.image.sized.MediaEntityKind
import wallapp.image.sized.SizedImage
```

Replace the block from `val keys = b.media.mediaMap.keys` through the end of the `for (f in b.content.folders)` loop with:

```kotlin
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
```

Leave checks (c), (d), and (e) — the content↔search id equality, the imgix pass-through invariant, and the strict re-decode — exactly as they are.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :service:content-pipeline:test`
Expected: `BUILD SUCCESSFUL`, all pipeline tests.

- [ ] **Step 5: Commit**

```bash
git add service/content-pipeline/src/main/kotlin/wallapp/pipeline/validate/CatalogValidator.kt \
        service/content-pipeline/src/test/kotlin/wallapp/pipeline/validate/CatalogValidatorTest.kt
git commit -m "fix(pipeline): validate the media map against the shared contract

CatalogValidator hardcoded an incomplete copy of the required key sets,
which is why it caught neither the missing collection-layer keys nor the
folder banner carrying a feed key. Adds independent unknown-key and
orphan/collision checks."
```

---

### Task 10: Add `--dry-run`, then prove the refactor changed nothing on the wire

`Main.kt` validates and then publishes in one shot. Before the first write into an empty prod bucket, we want to build and validate a manifest without touching R2.

**Files:**
- Modify: `service/content-pipeline/src/main/kotlin/wallapp/pipeline/Main.kt`
- Test: manual, against staging

**Interfaces:**
- Consumes: `CatalogValidator.validate(bundle)` (Task 9); `MediaMapBuilder.build` (Task 8).
- Produces: `pipeline <manifest.json> [--dry-run]`. Task 11 uses `--dry-run` before publishing to prod.

- [ ] **Step 1: Write the failing test**

Create `service/content-pipeline/src/test/kotlin/wallapp/pipeline/MainArgsTest.kt`:

```kotlin
package wallapp.pipeline

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainArgsTest {

    @Test fun dryRunFlagIsRecognisedInAnyPosition() {
        assertTrue(parseArgs(arrayOf("m.json", "--dry-run")).dryRun)
        assertTrue(parseArgs(arrayOf("--dry-run", "m.json")).dryRun)
        assertFalse(parseArgs(arrayOf("m.json")).dryRun)
    }

    @Test fun manifestPathIsTheFirstNonFlagArgument() {
        assertEquals("m.json", parseArgs(arrayOf("--dry-run", "m.json")).manifestPath)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :service:content-pipeline:test --tests '*MainArgsTest*'`
Expected: FAIL to compile — `Unresolved reference: parseArgs`

- [ ] **Step 3: Implement argument parsing and the dry-run path**

In `Main.kt`, add above `fun main`:

```kotlin
data class PipelineArgs(val manifestPath: String, val dryRun: Boolean)

fun parseArgs(args: Array<String>): PipelineArgs {
    val dryRun = args.any { it == "--dry-run" }
    val manifestPath = args.firstOrNull { !it.startsWith("--") }
        ?: error("usage: publish <manifest.json> [--dry-run]")
    return PipelineArgs(manifestPath, dryRun)
}
```

Change `fun main` to use it, and skip the `Publisher(...).publish(...)` call when `dryRun` is true — print the media export string instead, so the output can be diffed:

```kotlin
    CatalogValidator.validate(bundle)                       // fail-fast BEFORE any upload
    if (parsed.dryRun) {
        println("DRY RUN — validated ${m.version}, ${bundle.media.mediaMap.size} media entries, nothing uploaded")
        println(bundle.media.exportString)
        return
    }
    Publisher(putter, fetcher, m.baseUrl).publish(bundle, renditionFiles, m.version)
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :service:content-pipeline:test`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Prove the refactor is byte-identical against the live staging catalog**

The staging catalog `20260709-06` was published by the *old* builder. Rebuild it with the new one and diff. Nothing is written.

```bash
./gradlew :service:content-pipeline:run --args="/abs/path/to/staging-manifest.json --dry-run" -q \
  | sed -n '/^{/,$p' > /tmp/rebuilt-media.json

curl -sS "https://media-staging.stillscenes.app/api/20260709-06/media-1a-c-p~s" > /tmp/live-media.json

diff <(python3 -m json.tool --sort-keys /tmp/rebuilt-media.json) \
     <(python3 -m json.tool --sort-keys /tmp/live-media.json) && echo "IDENTICAL"
```

Expected: `IDENTICAL`.

The manifest lives outside the repo — `find . -name '*manifest*.json'` returns nothing. Ask the user for its path.

If the diff is non-empty, **stop.** Either the refactor changed the wire format (check `MediaEntityKind` declaration order against Task 8 Step 3) or the live catalog was never what the builder produces. Do not proceed to Task 11 until this prints `IDENTICAL`.

- [ ] **Step 6: Commit**

```bash
git add service/content-pipeline/src/main/kotlin/wallapp/pipeline/Main.kt \
        service/content-pipeline/src/test/kotlin/wallapp/pipeline/MainArgsTest.kt
git commit -m "feat(pipeline): add --dry-run to build and validate without publishing"
```

---

### Task 11: Publish the first prod catalog

> **GATED.** Requires Task 5 (the hostname exists) and Task 10 (the media map is validated and proven byte-identical). Until this task completes, **release builds are non-functional** — their in-app default names `20260709-06`, which prod cannot yet serve. That is acceptable only because no release build is in anyone's hands. Do not cut one before this lands.

**Files:**
- Modify: `docs/HANDOFF.md`

**Interfaces:**
- Consumes: `--dry-run` (Task 10); `https://media.stillscenes.app` (Task 5); the in-app default `20260709-06` (Task 2).
- Produces: a prod catalog. Nothing consumes it in code.

> **Corrected after the final review.** This task originally said "publish with `run --args=prod-manifest.json`"
> and listed no code change. `Main.kt` hardcoded `WranglerClient(bucket = "stillscenes-content-staging")`,
> and `Publisher` PUTs every object *before* it verifies. Run as written, it would have written
> prod-URL'd bytes over the live staging catalog `20260709-06`, then failed its read-back against the
> empty prod host — corrupting staging, leaving prod empty, and reporting failure after the damage.
>
> Fixed in `155107d`: the bucket is now **derived from the manifest's `baseUrl`**. A prod manifest can
> only reach `stillscenes-content-prod`; an unrecognised host publishes nowhere. There is deliberately
> no `--bucket` flag — a second way to declare the environment is a second way to declare it wrong.

- [ ] **Step 1: Prepare a prod manifest**

Copy the staging manifest, changing exactly two fields:

```json
  "version": "20260709-06",
  "baseUrl": "https://media.stillscenes.app",
```

Everything else — artist, folder, wallpapers, rendition paths — stays identical. Prod and staging start on the same bytes.

The `baseUrl` is now load-bearing twice over: it is both the host baked into every media-map URL **and**
the selector for the R2 bucket. `bucketFor()` maps `media.stillscenes.app` → `stillscenes-content-prod`
and `media-staging.stillscenes.app` → `stillscenes-content-staging`. Any other host is a hard error.

- [ ] **Step 2: Dry-run against the prod manifest**

```bash
./gradlew :service:content-pipeline:run --args="/abs/path/to/prod-manifest.json --dry-run"
```

Expected first line: `DRY RUN — validated 20260709-06 for bucket stillscenes-content-prod, N media entries, nothing uploaded`

**Read that bucket name before you go further.** If it says `stillscenes-content-staging`, the manifest's
`baseUrl` is still pointing at staging and Step 3 would republish over the live staging catalog.

If `CatalogValidator` throws here, the catalog is malformed. That is the whole point of the gate.

Note that `parseArgs` now rejects any unrecognised `--` flag. A typo'd `--dryrun` errors out instead of
silently performing a real publish.

- [ ] **Step 3: Publish**

```bash
./gradlew :service:content-pipeline:run --args="/abs/path/to/prod-manifest.json"
```

Expected final line: `Published version 20260709-06 to stillscenes-content-prod. Flip RC catalog_version to 20260709-06 to go live.`

The auto-mode classifier gates R2 writes. It requires the specific action named — "publish catalog 20260709-06 to stillscenes-content-prod". A bare "go" does not clear it.

The Publisher PUTs renditions → media/search/spec → **catalog last**, then verifies. Versions are write-once; an aborted verify leaves the bucket in a complete, unreferenced state, which is safe.

- [ ] **Step 4: Verify prod serves the catalog**

```bash
for path in content-1a content-metadata-1a media-1a-c-p~s; do
  printf '%s -> ' "$path"
  curl -sS -o /dev/null -w '%{http_code}\n' "https://media.stillscenes.app/api/20260709-06/$path"
done
```
Expected: `200` for all three.

- [ ] **Step 5: Verify a release build actually renders**

```bash
./gradlew :app:android:installWallAppRelease
adb shell am start -n app.stillscenes/wallapp.activity.MainActivity
```

Confirm: four wallpapers in **For You** and in **Explore**, a working free collection, and — critically — **no** `MediaMap entry missing` warnings, since the demo-catalog sentinel is gone.

```bash
adb logcat -d | grep -c "MediaMap entry missing"
```
Expected: `0`. Before this plan it was roughly 537 per launch.

- [ ] **Step 6: Update the handoff and commit**

In `docs/HANDOFF.md`, update the "Current production state" table: `stillscenes-content-prod` is no longer empty, and record that `catalog_version` = `20260709-06` now resolves against prod.

```bash
git add docs/HANDOFF.md
git commit -m "docs: prod bucket now serves catalog 20260709-06"
```

---

## Deviations from the spec, for the record

1. **The spec says `CatalogValidator` should not re-check required-key sets** ("it would only confirm the enum equals itself"). Task 9 re-checks them anyway. Reason: the checks *already exist* at `CatalogValidator:19,23` as an incomplete hardcoded copy. Deleting them removes a tripwire; replacing them removes the third source of truth while keeping it. The plan labels them as a tripwire rather than an independent check, and adds two checks that genuinely are independent (unknown-key scan, orphan/collision).

2. **The spec did not mention `--dry-run`** (Task 10). It was added because the spec's own gate — "do not publish to prod until the validator exists" — is unenforceable without a way to run the validator against a manifest without publishing.

## Self-review notes

- **Spec coverage:** Phase 1 → Task 1. Phase 2 → Tasks 2, 3, 4, 5, 11. Phase 3 → Tasks 6, 7, 8, 9, 10. The demo-sentinel fix (handoff item 5) is Task 2 Step 5, verified in Task 11 Step 5. The `.gitignore` gap noted in the spec's *Known gaps* is deliberately not a task — it is unrelated housekeeping.
- **Type consistency:** `requiredKeyStrings` (not `requiredStringKeys`) is used in Tasks 6, 8, 9. `fromOrNull` (not `fromKeyOrNull`) in Tasks 7, 9. `NamedScope.CatalogVersion` in Task 3 only. `parseArgs`/`PipelineArgs` in Task 10 only.
- **Known risk, Task 3 Step 10:** `WaeTestModule.kt:43` diverges from production wiring. Moving `ContentDeliveryConfig` out of `ContentModule` may break `DeliveryPathWiringTest`'s graph if the test module does not include `BuildConfigModule`. Called out inline.
- **Known risk, Task 10 Step 5:** the staging manifest is not in the repo. The step says to ask.
