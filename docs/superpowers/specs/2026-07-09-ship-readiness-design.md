# Ship readiness — package identity, environment split, media-map contract

**Date:** 2026-07-09
**Branch:** `feat/foundation-delivery`
**Status:** approved, pending implementation plan

Three independent changes that together take the app from "works on an emulator against staging"
to "shippable, and wrong catalogs get caught before they ship." They touch disjoint modules and
commit separately.

---

## Context

The app works end to end: a clean install authenticates against `stillscenes-prod`, pulls catalog
`20260709-06` from R2 through Cloudflare, and renders four wallpapers plus a free collection.

It is not shippable. It is packaged as `com.example.wallapp`, it hardcodes the staging CDN, and the
pipeline's media map is a stringly-typed contract that has silently broken twice.

See `docs/HANDOFF.md` for the state this design starts from.

---

## Decisions

| Question | Decision | Why |
|---|---|---|
| Package name | `app.stillscenes` | Reverse-DNS of `stillscenes.app`, the domain we control — which is what the convention actually asks for. |
| Prod/staging catalog version | Two RC keys, chosen by build type | Lets us publish to staging and preview it in a debug build without prod being able to serve that version yet. |
| Media-map validator | Shared contract enum | The root cause is two sources of truth for one fact. A shared enum cures that; a pipeline-local table would re-create it. |
| First prod publish | Gated on Phase 3 | The riskiest step in Phase 2 is the first write into an empty bucket. Phase 3 is what checks it. |

---

## Phase 1 — Package identity

`applicationId` is permanent once the app ships. Nothing has shipped, so this is the last cheap moment.

### Change surface

| File | Change |
|---|---|
| `app/android/android.gradle.kts:61` | `applicationId = "app.stillscenes"` |
| `app/desktop/desktop.gradle.kts:55` | `bundleID = "app.stillscenes.desktop"` |
| `app/desktop/desktop.gradle.kts:53` | `packageName = "MyProject"` → `"StillScenes"` (names the `.dmg`/`.msi`/`.deb`) |
| `app/android/google-services.json` | regenerated after the new app is registered |
| `docs/HANDOFF.md` | the `adb am start` line names the old package |

### Firebase steps

In project `stillscenes-prod` (unchanged, `809386236419`):

1. Register a new Android app for `app.stillscenes`.
2. Add the debug keystore's SHA-1 — `app/android/debug.keystore`, alias `androiddebugkey`,
   store/key password `android`. This mints the `client_type: 1` OAuth client.
3. Download the regenerated `google-services.json`.

Leave the old `com.example.wallapp` app registered until the new one is verified; it costs nothing
and preserves a fallback.

### What does not change

`namespace = "wallapp.app.android"` (`android.gradle.kts:44`) — AGP decouples `namespace` (where
classes, `R`, and `BuildConfig` live) from `applicationId` (device and Play Store identity). The
Kotlin package was never `com.example.*`. **Zero Kotlin source files move.**

`GoogleSignInFactory.kt:13` hardcodes `809386236419-d40uel...`, which `google-services.json` shows is
a **`client_type: 3`** — a *web* OAuth client. Web clients are scoped to the Firebase project, not to
an Android app. Android's Google Sign-In uses the web client id as its `serverClientId`; the
`client_type: 1` Android client exists only so Google can verify the caller's package and signature,
and no code names it. **This file does not change.** The handoff's claim that sign-in client ids must
be redone is wrong.

Firestore and Storage rules key on `uid`. Remote Config is project-scoped. `android.gradle.kts:247`
already interpolates `${variant.applicationId}`, so the `run` task self-updates.

`NamedScope.ApplicationId` resolves through `Factory.android.kt:599` as `context.packageName`, so it
self-updates too.

### Out of scope

- `baselineprofile/build.gradle.kts:10` — `namespace = "com.example.baselineprofile"`. A test module's
  own `R` class; invisible to the shipped app.
- The iOS bundle id. iOS is unverified (handoff item 4); renaming it blind adds risk with no way to check it.
- `Factory.desktop.kt:143` and `Factory.ios.kt:172` hardcode `applicationId = "com.wallapp.example"` —
  a **third** string, matching neither the old name nor the new one, feeding `FlavorConfig`. Already
  wrong today. Not fallout from this rename; do not "fix" it here.

### Verification

`:shared:app:app-adapter:desktopTest` stays green — no shared code is touched. Then install the debug
build and complete Google Sign-In. That is the only step that exercises the new SHA-1 and the new
`client_type: 1` client. A missing or wrong SHA-1 surfaces as `DEVELOPER_ERROR` (status code 10).

### Risk

The failure mode is loud and immediate: sign-in breaks. Rollback is one line plus the old
`google-services.json`.

---

## Phase 2 — Environment split

### The binding move

Do **not** add a `catalogVersionKey` field to `ContentDeliveryConfig`. `shared/data/remoteendpoint`
does not depend on `shared/data/remoteconfig-api`, and naming an RC entry inside the delivery config
would force a module edge from the delivery layer to the config layer to express a fact neither needs.

`RemoteEndpointsRepositoryRemoteConfig` already takes a bare `StateFlow<String>` and has no idea which
key produced it. Vary the flow, not the config object. Both environment facts then live in one place.

```kotlin
// ContentModule.kt — environment-agnostic; the ContentDeliveryConfig binding is removed
single<RemoteEndpointsRepositoryNetwork> {
    RemoteEndpointsRepositoryRemoteConfig(catalogVersion = get(NamedScope.CatalogVersion), config = get())
}

// di-buildconfig-release/BuildConfigModule.kt
single<ContentDeliveryConfig> { ContentDeliveryConfig(baseUrl = "https://media.stillscenes.app") }
single<StateFlow<String>>(NamedScope.CatalogVersion) { get<RemoteConfigData>().catalogVersion }

// di-buildconfig-debug/BuildConfigModule.kt
single<ContentDeliveryConfig> { ContentDeliveryConfig(baseUrl = "https://media-staging.stillscenes.app") }
single<StateFlow<String>>(NamedScope.CatalogVersion) { get<RemoteConfigData>().catalogVersionStaging }
```

`di-base.gradle.kts:101-143` already selects these modules per build type: `androidDebug`/`androidRelease`
source sets, an iOS `System.getenv("CONFIGURATION")` check, and desktop pinned to debug.

### New Remote Config surface

In `shared/data/remoteconfig-api`:

- `RemoteConfigKey.CatalogVersionStaging` → `"catalog_version_staging"`
- `RemoteConfigEntry.CatalogVersionStaging`
- `RemoteConfigDataDefaultsProvider.catalogVersionStaging`
- `RemoteConfigData.catalogVersionStaging: StateFlow<String>`

The `when` in `toRemoteConfigEntry()` is exhaustive over `RemoteConfigKey`, so the compiler refuses to
build until every piece is wired. `asDefaultsArray()` iterates `RemoteConfigKey.entries`, so the new
key registers as a default automatically.

### Demo-sentinel fix (handoff item 5)

`RemoteConfigDataDefaultsProvider.catalogVersion` is `"99999999"`, the demo catalog. Every cold start
fetches its 219 items before RC activates, then discards them — the source of the ~537
`MediaMap entry missing` warnings per launch.

- `catalogVersion` default → `20260709-06`
- `catalogVersionStaging` default → `20260709-06`

Both name the same version deliberately: the first prod publish writes the *same bytes* that staging
already serves, so the two environments start identical and any later divergence is intentional.

**Release builds are non-functional between this code landing and the prod publish.** The default names
`20260709-06`, and `stillscenes-content-prod` will not contain it until the publish step runs (gated on
Phase 3, below). Leaving the default at `99999999` would not help — the demo catalog does not exist in
prod either. This window is safe only because no release build is in anyone's hands. Do not cut a
release build until the prod publish has completed.

Two tests assert the sentinel and must change: `RemoteEndpointsRemoteConfigTest.kt:20` and
`DeliveryPathWiringTest.kt:26`. Both pin a constant rather than a behavior.

### Infrastructure

Blocked on the rotated Cloudflare token (see *Dependencies*). In order:

1. R2 custom domain `media.stillscenes.app` → bucket `stillscenes-content-prod`. Cloudflare
   automatically adds the CNAME record. **Connecting a custom domain makes the bucket publicly
   readable** — intended for a CDN origin, but it means the moment this step runs, anything in
   `stillscenes-content-prod` is world-readable. The bucket is empty, so run this before the publish.
2. Replicate the extensionless-`/api/` cache rules that today exist only for the staging hostname.
   This is required, not an optimization: Cloudflare does **not** cache all file types by default —
   JSON and HTML are excluded unless a Cache Rule says otherwise, and our catalog objects are
   extensionless (`/api/<version>/content-1a`). Without the rule every request is a cache miss that
   bills an R2 Class B operation.
3. Add `catalog_version_staging` to `firebase-backend/remoteconfig.template.json` and
   `firebase deploy --only remoteconfig -P stillscenes-prod` **before** the code lands.

On (3): Remote Config silently serves the compiled-in default for any key the server does not define.
A missing `catalog_version_staging` would therefore not error — it would quietly use the in-app
default. Here that default is correct, so the trap is harmless. It would not be harmless if the
default were stale. Deploy first anyway.

### The prod publish is gated on Phase 3

`stillscenes-content-prod` is empty. Land Phase 2's code, RC, and Cloudflare infra, but do not publish
a catalog into prod until Phase 3's validator exists. No release build is in anyone's hands, so
waiting costs nothing, and it means the first bytes to reach prod are the first ones ever cross-checked.

The publish itself: run the pipeline against a prod manifest (`baseUrl` → `https://media.stillscenes.app`,
bucket → `stillscenes-content-prod`) at version `20260709-06`, which is already the in-app default. No
Remote Config change is needed at publish time; `catalog_version` already reads `20260709-06`.

The auto-mode classifier gates R2 writes and prod RC deploys. It requires the *specific* action named
("publish catalog 20260709-06 to stillscenes-content-prod"); a bare "go" does not clear it.

### Testing

`app-adapter:desktopTest` links `di-buildconfig-debug` (`di-base.gradle.kts:141`, *"Desktop always uses
Debug for now"*), so **no desktop test can observe the release binding.** Each buildconfig module gets
its own test asserting its own `BuildConfigModule`. That is the only place the prod URL is assertable,
and it is a wiring assertion rather than a constant restated in two files.

`DeliveryPathWiringTest` stays, but its meaning changes from *"the base URL is this string"* to
*"the desktop/debug graph resolves to staging, not prod."*

Resolving `NamedScope.CatalogVersion` in a module test requires `RemoteConfigData` in the graph; use a
stub, or assert only the `ContentDeliveryConfig` binding if a stub proves disproportionate.

---

## Phase 3 — Media-map contract

`NetworkMediaMap.kt:3` reads `typealias NetworkMediaMap = Map<String, String> /** SizedImage.key -> url */`.
That comment is the entire contract between `MediaMapBuilder` and the renderers. Two bugs
(`c2db6cf`, `fa8453e`) came from it, and both were invisible to unit tests *and* to the CDN read-back,
because the map was internally consistent and the bytes arrived intact.

### The contract, stated once

New `MediaEntityKind`, beside `SizedImage` in `shared/data/base`:

```kotlin
enum class MediaEntityKind(val requiredKeys: Set<SizedImage>) {
    WallpaperDownload(setOf(DownloadableWallpaperHd, DownloadableWallpaperSd)),
    WallpaperPreview(setOf(Showcase, WallpaperFeedSingle, WallpaperFeedTrack, FullScreen,
        WallpaperCollectionSmallLayer0, WallpaperCollectionSmallLayer1, WallpaperCollectionSmallLayer2,
        WallpaperCollectionLargeLayer0, WallpaperCollectionLargeLayer1, WallpaperCollectionLargeLayer2)),
    ArtistProfile(setOf(ArtistMedium, ArtistSmall, Exhibit)),
    FolderProfile(setOf(WallpaperFeedSingle)),
    FolderBanner(setOf(Exhibit)),
}
```

`service/content-pipeline` adds `implementation(project(":shared:data:base"))`. This edge is already
established: the pipeline is `kotlin("jvm")` and already depends on `:shared:data:mediamap-network` and
`:shared:data:content-network`, which carry the identical `androidTarget()/desktop()/iOS()` target
triple as `data:base`.

`MediaMapBuilder` derives each entry as `kind.requiredKeys.associate { it.key to url }`. Its private
`CollectionLayerKeys` list of string literals is deleted.

### What each mechanism catches — and what it does not

- **A `SizedImage` nothing emits.** A test asserting
  `SizedImage.entries - MediaEntityKind.entries.flatMap { it.requiredKeys }` is empty. This *would*
  have caught `c2db6cf`: `wcs0`…`wcl2` existed in `SizedImage` while no map entry carried them.
- **A typo'd key.** Impossible by construction once the builder derives from the enum; caught at
  ingestion once `SizedImage.from()` stops falling back.
- **The wrong key on the right entity** — `fa8453e`, the folder banner carrying `wfs` instead of `e`.
  **Not caught.** `Exhibit` was already emitted by `ArtistProfile`, so no union check fires. What
  changes is that the fact moves from a buried `mapOf("e" to url(...))` into a one-line declaration a
  reviewer reads as intent. That is a real improvement and it is not detection.
- **Enum-vs-renderer drift.** **Not covered.** Closing it needs a hand-maintained render-site-to-entity
  map, which is itself a second source of truth — the disease this phase exists to cure. Explicitly
  declined.

### The silent-alias bug

Independent of the above. `MediaMapMapper.kt:15` does `mediaMap[SizedImage.from(sizedImage)] = ImageModel.from(url)`,
and `SizedImage.from()` ends in `?: Preset`. A wire key of `"wsc0"` resolves to `WallpaperFeedSingle`
and **overwrites the real `wfs` entry**, corrupting the feed image for every wallpaper, with no warning.

- `from()` becomes `fromOrNull()`; the single caller drops-and-logs unknown keys.
- The `require(entries.map { it.key }.distinct().size == entries.size)` duplicate-key check currently
  runs on *every call*. It moves to a test.

### Validator scope

`CatalogValidator` gains a media-map pass that recomputes expected `mediaId`s from the manifest and checks:

- every emitted key parses to a known `SizedImage`
- every manifest entity has a map entry
- no orphan entries
- no `mediaId` collisions

It does **not** re-check required-key sets. The builder derives those from the enum, so re-deriving
them in the validator would only confirm the enum equals itself.

### Verification

`./gradlew :service:content-pipeline:test` and the `data:base` tests. Then the real proof: republish
catalog `20260709-06` to **staging** and confirm the emitted media map is byte-identical to what is
already live. Identical bytes mean the refactor changed nothing. A diff means either the refactor or
the current catalog is wrong — and either way we want to know before Phase 2's prod publish.

---

## Dependencies and order

```
Cloudflare token rotation (user)  ──────► Phase 2 infra
Phase 1 (independent)
Phase 3 (independent)  ─────────────────► Phase 2 prod publish
```

Phase 1 and Phase 3 are independent of everything and of each other. Phase 2's code and Remote Config
work is independent; its Cloudflare steps need the rotated token, and its prod publish needs Phase 3.

Secret rotation (the Cloudflare scoped token and the BFL API key, both pasted into chat and both live)
is the user's to perform and is not part of any phase. Phase 2's infrastructure work cannot start
until the new Cloudflare token exists.

---

## Verification commands

```bash
# App: use desktopTest, NOT test. :testDebugUnitTest fails on a clean tree (pre-existing).
./gradlew :shared:app:app-adapter:desktopTest :shared:domain:content-state:desktopTest \
          :shared:data:content:desktopTest :shared:data:remoteconfig-api:desktopTest \
          :shared:data:remoteapi:desktopTest

# Pipeline
./gradlew :service:content-pipeline:test

# Launch with the explicit activity: `monkey -c LAUNCHER` starts LeakCanary's activity.
adb shell am start -n app.stillscenes/wallapp.activity.MainActivity
```

---

## Known gaps this design does not close

- **Enum-vs-renderer drift** (Phase 3, above). Declined as disproportionate.
- **iOS is unverified.** Everything is driven on the Android emulator.
- **The unexplained ANR** (handoff item 6) is not addressed and is not claimed fixed.
- **`image_host_name` is a dead RC param** and `highlight_just_added` is unread by the client
  (handoff items 10, 11). Untouched.
- **`.gitignore` covers none of `node_modules/`, `.wrangler/`, `learn/`**, all currently untracked.
  A single `git add -A` would commit them. Not part of any phase; worth a standalone commit.
- **`com.google.android.gms.auth.api.signin.GoogleSignIn` is deprecated** in favour of Credential
  Manager, and is used in `GoogleAuthManager.android.kt`, `AccountPlatformModule.android.kt`, and
  `MainActivity.kt`. Phase 1 does not touch it and does not need to. Noted because a future migration
  will revisit the same client ids.

---

## External behaviour verified while writing this spec

Each of these is load-bearing above and was checked against current documentation rather than recalled:

- `namespace` and `applicationId` are decoupled; `applicationId` defaults to `namespace` only when it
  is not explicitly set (it is, here) — [Configure the app module](https://developer.android.com/build/configure-app-module)
- `client_type: 1` is the Android OAuth client, `client_type: 3` the web client; `requestIdToken` /
  `setServerClientId` take the **web** client id — [Authenticate with Google on Android](https://firebase.google.com/docs/auth/android/google-signin)
- `DEVELOPER_ERROR` (status 10) is caused by an unregistered/mismatched SHA-1, or by passing the
  Android client id where the web one is required — [Common Google sign in error, constant value 10](https://artemcodes.medium.com/common-google-sign-in-error-in-android-error-constant-value-10-127fe2f2d78e)
- An `applicationId` cannot be changed after publishing; Play treats a changed id as a new app —
  [Configure the app module](https://developer.android.com/build/configure-app-module)
- Remote Config serves the in-app default for any parameter absent from the backend —
  [Remote Config Parameters and Conditions](https://firebase.google.com/docs/remote-config/parameters)
- An R2 custom domain auto-adds a CNAME and makes the bucket publicly readable; Cloudflare does not
  cache JSON/HTML by default and needs an explicit Cache Rule —
  [Public buckets](https://developers.cloudflare.com/r2/buckets/public-buckets/),
  [Enable cache in an R2 bucket](https://developers.cloudflare.com/cache/interaction-cloudflare-products/r2/)
