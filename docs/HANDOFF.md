# Handoff — StillScenes / wallapp

**As of:** 2026-07-09, end of session
**Branch:** `feat/foundation-delivery` (45 commits ahead of `main`, pushed, in sync with origin)
**PR:** [#1](https://github.com/khanhubban/wallapp/pull/1) — open, `MERGEABLE`/`CLEAN`, no CI configured

---

## TL;DR

The app works end to end. A clean install authenticates against our Firebase project, pulls catalog
`20260709-06` from R2 through Cloudflare, and renders four AI-generated wallpapers in both **For You**
and **Explore**, with a working free collection. Zero crashes, zero missing media lookups.

Nothing is shipped. The app still points at the **staging** CDN and is still packaged as
`com.example.wallapp`.

---

## Current production state

| What | Value |
|---|---|
| Firebase project | `stillscenes-prod` (809386236419) |
| Remote Config | version 4 |
| `catalog_version` | `20260709-06` |
| `highlight_collection_of_the_week` | `stillscenes~featured` |
| CDN | `media-staging.stillscenes.app` → R2 `stillscenes-content-staging` |
| Catalogs live | `20260709-03`, `-04`, `-05`, `-06` (all 200; write-once, older ones are rollback targets) |
| `stillscenes-content-prod` bucket | **empty** |

**Rollback:** Remote Config keeps version history. Reverting `catalog_version` to `20260709-03` restores
the pre-collection state (that catalog is intact). Do it from the Firebase console or by editing
`firebase-backend/remoteconfig.template.json` and running
`firebase deploy --only remoteconfig -P stillscenes-prod`.

---

## Verifying you haven't broken anything

```bash
# App: 371 tests. Use desktopTest, NOT test.
./gradlew :shared:app:app-adapter:desktopTest :shared:domain:content-state:desktopTest \
          :shared:data:content:desktopTest :shared:data:remoteconfig-api:desktopTest \
          :shared:data:remoteapi:desktopTest

# Pipeline: 29 tests.
./gradlew :service:content-pipeline:test
```

`:shared:app:app-adapter:testDebugUnitTest` **fails on a clean tree** (`Method myPid in
android.os.Process not mocked`, via `ConfigValueRepositoryFirebase`). It is pre-existing and unrelated
to this branch. `:test` runs both, so it always looks red. Use `:desktopTest`.

On the emulator, launch with the explicit activity — `adb shell monkey -c LAUNCHER` starts
**LeakCanary's** launcher activity, not the app:

```bash
adb shell am start -n com.example.wallapp/wallapp.activity.MainActivity
```

---

## What landed this session (11 commits)

Everything below was found by driving the real app, not by reading code.

| Commit | What |
|---|---|
| `d417f8d` | key1 (Firebase Storage) absence no longer reported as a fatal network error |
| `2e89721` | **the blank-screen fix** — `requiresEncryptionKey` |
| `af8ef8d` | a Remote Config category id can no longer crash every client |
| `9224b7b` | highlight defaults → real catalog ids; RC template made deployable |
| `89d44ca` | onboarding clamps to the number of artists that exist |
| `ffeca64` | **the Explore-spinner fix** — render a singles-only catalog |
| `cbb9c31` | RC template docs corrected |
| `981116d` | **free collections** + `CatalogBuilder` emits one |
| `c2db6cf` | media map carries collection-layer preview keys |
| `fa8453e` | folder banner carries `Exhibit` key |
| `3a847de` | Publisher read-back consults origin before failing |

### The three root causes worth remembering

**1. Blank screen.** `NetworkRefreshManager` gated *every* content fetch behind a non-blank encryption
key: `key.first { it.isNotBlank() }`. `key1` lives in Firebase Storage, which the R2 delivery path never
provisions, so the key stayed `""` and that suspend never resumed — no endpoints, no content, no media
map. Making the missing key merely *non-fatal* (`d417f8d`) turned a reported error into a **silent
hang**, which is harder to diagnose. The real fix is `RemoteApiEncryptionConfig.requiresEncryptionKey`,
consulted at all three gate sites.

**2. Explore spinner.** `ViewStateFactoryDefault` renders a null feed and a still-loading feed
*identically* (`if (items.isNullOrEmpty()) return ExploreViewState.Loading`). The feed was null because
the mapper early-exited on empty collections+highlights, and both were empty because the catalog had no
`Collection`-type category. Also: `categoryHighlights` collapsed "loaded, no collection" to `null` via
`.ifEmpty { null }` — the same sentinel `stateIn` uses for "not loaded yet" — stalling
`exploreHighlights` forever.

**3. Collections are IAP bundles.** You cannot just add a `Collection` to a catalog. The model
`require`d `purchasableProductIds` in two places and `isUnlocked = isPurchased || isUnlockedViaSubscription`.
A Collection built from free singles would have crashed at *catalog decode*. Hence free collections:
`WallpaperCategory.isFree`, `CollectionPurchasable.orNull()`, `CollectionConnectionState.isFree`.

---

## Traps that will bite you

**Remote Config silently serves in-app defaults** for any key the server does not define. A log line
reading `[RemoteConfig] "highlight_artist": a~indigo` may be `RemoteConfigDataDefaultsProvider`, not the
server. Do not read those logs as proof of delivery. Check with
`firebase remoteconfig:get -P stillscenes-prod`.

**The collection id must not end in `~singles`.** `splitByCategoryType()` classifies by
`id.endsWith("~singles")`, *independently* of the declared `categoryType`. Two sources of truth for the
same fact.

**A passing test can encode a bug.** `MediaMapBuilderTest` asserted `assertEquals(setOf("wfs"),
banner.keys)` — the wrong shape — and passed, defending the bug that left "Just Added" with a blank
background since the pipeline's first catalog. Corrected in `fa8453e`.

**The media map is a stringly-typed contract** between `MediaMapBuilder` and `SizedImage.kt`. Nothing
checks that the keys a renderer asks for are keys the builder emits. Two bugs (`c2db6cf`, `fa8453e`)
came from this, and both were invisible to tests *and* to the CDN read-back, because the map was
internally consistent and the bytes arrived intact. **A validator cross-referencing `SizedImage`
entries against emitted keys would catch the whole class.** Recommended.

**Enabling a code path dereferences bugs the disabled path was hiding.** Happened three times on this
branch: a negative `subList` bound, a divide-by-zero in `processMultipleContentStateCollections`, and
the two media-map key gaps.

**Reference for wire format:** the demo catalog at
`https://media-staging.stillscenes.app/api/99999999/content-1a` and its media map. It has 9 real
`Collection` categories and 36 entries carrying the collection-layer keys. When unsure what shape the
pipeline should emit, diff against it.

---

## Publishing a new catalog

```bash
# Always dry-run first. It builds and validates, and constructs no R2 or HTTP client.
./gradlew :service:content-pipeline:run --args="/abs/path/to/manifest.json --dry-run"
# Read the bucket name it prints. Then:
./gradlew :service:content-pipeline:run --args="/abs/path/to/manifest.json"
# then bump the catalog version key it names in firebase-backend/remoteconfig.template.json
firebase deploy --only remoteconfig -P stillscenes-prod
```

**The bucket is derived from the manifest's `baseUrl`**, not passed as a flag —
`media.stillscenes.app` → `stillscenes-content-prod`, `media-staging.stillscenes.app` →
`stillscenes-content-staging`, anything else is a hard error. This is deliberate: `baseUrl` already
declares the environment, and a second way to declare it is a second way to declare it wrong.
Before `155107d` the bucket was hardcoded to staging, and publishing a prod manifest would have
overwritten the live staging catalog with prod-URL'd bytes before failing its read-back.

`--dry-run` prints the target bucket. **Read it.** `parseArgs` rejects unknown `--` flags, so a
typo'd `--dryrun` errors instead of quietly performing a real publish.

The Publisher PUTs renditions → media/search/spec → **catalog last**, then verifies. Note the writes
all happen *before* the verify: an aborted read-back means the objects landed anyway. Versions are
write-once by convention, not by enforcement — a re-PUT of the same version overwrites it.

If an older build aborts with `read-back byte mismatch for media/artist/…`, **the objects did land** —
that was the edge answering from `immutable` cache with the object we just overwrote. Fixed in
`3a847de`; the read-back now retries with `?cacheBust=N` and only fails when origin genuinely differs.

The auto-mode classifier gates R2 writes and prod RC deploys. It requires the *specific* action named
("publish catalog X to bucket Y", "flip RC to X"); a bare "go" does not clear it.

---

## Open work, roughly in priority order

### Ship blockers

1. **App points at staging.** `shared/di/di-base/.../ContentModule.kt:130` hardcodes
   `https://media-staging.stillscenes.app` with a `TODO(release plan)`. The `stillscenes-content-prod`
   bucket is **empty**. Needs per-buildtype config + a publish to prod + a decision on whether prod and
   staging share one `catalog_version`.
2. **Package is still `com.example.wallapp`** (`app/android/android.gradle.kts:61`). The StillScenes
   rebrand never landed. Changing it means a new Firebase Android app, new `google-services.json`, new
   SHA-1s, and re-doing the Google sign-in client ids. Cheap now, expensive after the first install.
3. **Rotate two secrets** that were pasted into chat: the Cloudflare scoped token (`cfut_…`) and the
   BFL API key. Both are live.

### Correctness / quality

4. **iOS is unverified.** Everything was driven on the Android emulator. The free-collection model
   change and `exploreHighlights` (now 5 items) both touch shared KMP code. The all-or-nothing guard in
   `ShowcaseRepositoryDefault.exploreHighlights` exists because of a known iOS carousel render bug tied
   to item count.
5. **Every cold start fetches the demo catalog.** In-app `catalogVersion` defaults to the `99999999`
   sentinel (`RemoteConfigDataDefaultsProvider.kt:15`), so the app pulls `api/99999999` (219 items)
   before RC activates, then swaps to the real catalog (5 items). This is the source of the ~537
   `MediaMap entry missing` warnings on every launch. Wasted network + unreadable logs.
6. **Unexplained ANR.** Seen once: `failed to complete startup`, main thread in
   `handleBindApplication` → binder transact, on a LeakCanary debug build. Did not reproduce. Never
   root-caused. Not claimed fixed.
7. **RC rollback drill** never run. Now easy: RC has version history and catalog `20260709-03` is intact.
8. **Media-map key validator** (see traps above) — would have caught two of this session's bugs at
   build time.

### Product gaps

9. **Plus carousel card renders a literal `(placeholder)`** — `imageRepository.plusHighlightBackground`
   has no art, so `PlaceholderLabel.kt` shows its default string. Design asset, not code.
10. `image_host_name` Remote Config param is **dead** — no consumers.
    `ImageHostUrlMapperConfigImgix` hardcodes `https://<appname>.imgix.net`.
11. `highlight_just_added` is **not read** by the client; the Just Added row resolves
    `FolderDefinitions.FolderIdJustAdded` directly.
12. Only one artist and four wallpapers exist. `OnboardingManagerDefault.artistFollowOnboardingCount`
    is still `2`, clamped at runtime by `HomeOnboardingViewModel.requiredFollowCount()`.

### Housekeeping

13. Delete the duplicate Firebase project `stillscenes-pr` (941444940724).
14. `WaeTestModule.kt:43` binds `NetworkMediaMapRepository` to the old `NetworkMediaMapRepositoryNetwork`,
    diverging from production wiring.
15. `learn/` is untracked — commit or ignore it.
16. Cloudflare WAF skip rule still pending (precautionary; nothing is being blocked).

---

## Machine quirks

- Gradle is pinned to JDK 17 in `~/.gradle/gradle.properties`. `/usr/libexec/java_home -v 17` does
  **not** resolve the Homebrew keg; use `/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home`.
- Python 3.14.6 on this machine has a **broken `xml.etree.ElementTree`** (`pyexpat` symbol mismatch).
  Parse Gradle JUnit result XML with `grep`/`sed`, not Python.
- `pytest` is not installed and PEP 668 blocks `pip install`; pipeline scripts use stdlib `unittest`.
- Firebase rules tests need IPv4-first DNS (already in the npm test script).
