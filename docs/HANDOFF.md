# Handoff — StillScenes / wallapp

**As of:** 2026-07-10
**Branch:** `feat/foundation-delivery` (62 commits ahead of `main`)
**PR:** [#1](https://github.com/khanhubban/wallapp/pull/1) — open, no CI configured

---

## TL;DR

The app works end to end against **staging**. Phase 3 (the media-map contract) and the code half of
Phase 2 (the prod/staging split) are done, reviewed, and green: **419 KMP tests + 42 pipeline tests, 0
failures.**

Nothing is shipped. Four tasks remain and **every one of them needs a human at a console.** Release
builds are knowingly non-functional until the first prod publish lands.

Authoritative state lives in `.superpowers/sdd/progress.md`. **Trust it over anyone's recollection,
including a fresh session's reading of this file.** The other `progress-*.md` files in that directory
belong to *different, completed* plans — do not read them as current.

- Spec: `docs/superpowers/specs/2026-07-09-ship-readiness-design.md` (read its **Corrections** section)
- Plan: `docs/superpowers/plans/2026-07-10-ship-readiness.md` (11 tasks; 7 done)

---

## Current state

| What | Value |
|---|---|
| Firebase project | `stillscenes-prod` (809386236419) |
| Remote Config | version 5 (deployed 2026-07-10; version 4 is the rollback target) |
| `catalog_version` | `20260709-06` — **release builds read this** |
| `catalog_version_staging` | `20260709-06` — deployed (Task 4). Debug builds read this. |
| CDN, debug builds | `media-staging.stillscenes.app` → R2 `stillscenes-content-staging` — healthy, `HTTP 200` |
| CDN, release builds | `media.stillscenes.app` → R2 `stillscenes-content-prod` — **bucket empty**, `HTTP 404` |
| `applicationId` | `app.stillscenes` (Task 1 done 2026-07-10) |

`media.stillscenes.app` already answers *through Cloudflare* (`server: cloudflare`, a `cf-ray` header,
`404` rather than a DNS failure). The R2 custom domain appears already attached from the 2026-07-07
infra work. Verify before re-creating it.

**Rollback:** Remote Config keeps version history. Catalogs `20260709-03` … `-06` are all intact in
staging and are write-once, so any is a rollback target.

---

## Verifying you haven't broken anything

```bash
# KMP modules: desktopTest, NEVER :test.
./gradlew :shared:app:app-adapter:desktopTest :shared:domain:content-state:desktopTest \
          :shared:data:content:desktopTest :shared:data:remoteconfig-api:desktopTest \
          :shared:data:remoteapi:desktopTest :shared:data:base:desktopTest \
          :shared:data:mediamap:desktopTest \
          :shared:di:di-buildconfig-release:desktopTest :shared:di:di-buildconfig-debug:desktopTest

# Pipeline: 42 tests. This module is kotlin("jvm") with NO desktop target — its task IS `test`.
./gradlew :service:content-pipeline:test
```

`:shared:app:app-adapter:testDebugUnitTest` **fails on a clean tree** (`Method myPid in
android.os.Process not mocked`, via `ConfigValueRepositoryFirebase`). Pre-existing, unrelated. `:test`
runs it, so `:test` always looks red. If you find a failing JUnit XML, check its `mtime` — stale
`testDebugUnitTest` results linger in `build/`.

Launch with the explicit activity; `adb shell monkey -c LAUNCHER` starts **LeakCanary's** activity:

```bash
adb shell am start -n app.stillscenes/wallapp.activity.MainActivity
```

---

## What landed this session

| Commit | What |
|---|---|
| `63c09f1` `f219241` | `MediaEntityKind` — the media-map key contract, stated once |
| `e90b049` | `SizedImage.from()` → `fromOrNull`; unknown wire keys no longer clobber `wfs` |
| `13e329e` | `MediaMapBuilder` derives from the enum — byte-identical output |
| `edd2a23` `305f43b` | `CatalogValidator` checks the contract, plus orphan/collision checks |
| `b277413` | `catalog_version_staging` added; `99999999` demo sentinel retired |
| `d73ce4f` | CDN host + catalog key resolve per build type via `NamedScope.CatalogVersion` |
| `61e5cc2` | pipeline `--dry-run` |
| `155107d` | **publish bucket derived from the manifest; unknown flags rejected; `sdMediaId` shape-checked** |

---

## Traps that will bite you

**The publish bucket is derived from the manifest's `baseUrl`.** `media.stillscenes.app` →
`stillscenes-content-prod`; `media-staging.stillscenes.app` → `stillscenes-content-staging`; anything
else is a hard error. There is deliberately **no `--bucket` flag** — a second way to declare the
environment is a second way to declare it wrong. Before `155107d` the bucket was hardcoded to staging,
and publishing a prod manifest would have overwritten the live staging catalog.

**`Publisher` PUTs every object before it verifies.** An aborted read-back means the bytes landed
anyway. Versions are write-once by *convention*, not enforcement — a re-PUT of an existing version
overwrites it.

**`MediaEntityKind`'s declaration order determines published CDN bytes.** `setOf(...)` is a
`LinkedHashSet` and JSON key order follows map iteration order. Alphabetizing that enum silently changes
every catalog the pipeline emits. `MediaMapBuilderTest`'s wire-key-order tests are what catch it.

**`Set.equals()` is order-insensitive.** A test asserting `assertEquals(setOf(...), someSet)` cannot
observe ordering, no matter how load-bearing that ordering is. Compare `.toList()`. This bug shipped in
this plan's own tests and was caught in review.

**`shared/data/remoteconfig-firebase` has `desktop()` commented out.** No `desktopTest` compiles
`RemoteConfigDataDefault.kt` — the *production* Remote Config implementation. It has zero CI-reachable
coverage. "The tests passed" means less than it sounds like.

**Remote Config silently serves in-app defaults** for any key the server does not define. A log line
showing the right value proves nothing. Check with `firebase remoteconfig:get -P stillscenes-prod`.

**The collection id must not end in `~singles`.** `splitByCategoryType()` classifies by
`id.endsWith("~singles")`, *independently* of the declared `categoryType`.

**The emulator cannot test Google Sign-In, so Task 1 Step 10 has never actually run.** The attached AVD is
`sdk_phone64_arm64-userdebug` — a **plain AOSP image with zero Google packages**: no Play Services, no Play
Store, no Google account. Tapping sign-in yields `ApiException: 12500 SIGN_IN_FAILED` (seen 2026-07-10),
which is GMS being absent, **not** a certificate problem — a wrong SHA-1 presents as `10 / DEVELOPER_ERROR`.
Step 10 needs a **Google APIs / Play Store** system image. Until then the SHA-1 fix below is verified
statically (`apksigner` signer ∈ `google-services.json`) but **never at runtime**.

**There are two debug keystores, and Firebase trusts the wrong one.** Found 2026-07-10.
`app/android/debug.keystore` (checked in) is what Gradle signs with — `configureSigningConfigDebug` at
`android.gradle.kts:206` calls `project.file("debug.keystore")`, which resolves under `app/android/`.
Confirmed against the artifact: `apksigner verify --print-certs` on the built debug APK reports
`65355edc27991ab4812fc8613671a4acdb109751`. But `google-services.json` registers
`66c9cc786c5dabd4ef6bd0f1d9ebfbcc734abcd2`, which is `~/.android/debug.keystore` — the user-global one an
earlier plan doc (`2026-07-02-foundation-delivery-path.md:78`) told the operator to read.
**So Google Sign-In on `com.example.wallapp` has never worked**, and nothing catches it: no test exercises
sign-in, and the app browses content fine without it. `GoogleSignInFactory` uses legacy
`requestIdToken(webClientId)`, which validates `(package, SHA-1)` against a registered Android OAuth client
and throws `ApiException` status 10 `DEVELOPER_ERROR` on a mismatch. The web client id is fine; only the
certificate hash is wrong. Task 1 Step 4 fixes this by accident — as long as you register the *repo*
keystore's hash. Add it to the old app too, or your "rollback" is a build that cannot sign in.

**Task-scoped review is blind to code that didn't change.** Nine per-task reviews missed that `Main.kt`
hardcoded the staging bucket, because that line was never in a diff. The plan changed the world around
it. When a change adds a *thing* (a second bucket, a second environment), grep for code that assumed
there was only one.

**Reference for wire format:** the demo catalog at
`https://media-staging.stillscenes.app/api/99999999/content-1a`.

---

## Publishing a new catalog

```bash
# Always dry-run first. It builds and validates, and constructs no R2 or HTTP client.
./gradlew :service:content-pipeline:run --args="/abs/path/to/manifest.json --dry-run"
```

Its first line names the **target bucket**. Read it. Then:

```bash
./gradlew :service:content-pipeline:run --args="/abs/path/to/manifest.json"
# then bump the catalog version key it names, in firebase-backend/remoteconfig.template.json
firebase deploy --only remoteconfig -P stillscenes-prod
```

`parseArgs` rejects unknown `--` flags, so a typo'd `--dryrun` errors instead of quietly publishing.

The auto-mode classifier gates R2 writes and prod RC deploys. It requires the *specific* action named
("publish catalog 20260709-06 to stillscenes-content-prod"); a bare "go" does not clear it.

**The manifests are not in this repo.** `find . -name '*manifest*.json'` returns nothing. Ask the operator.

---

## Remaining work — three items, all needing a human

**Task 10 Step 5 is done (2026-07-10): the rebuilt media map is `BYTE-IDENTICAL` to live staging, 4820
bytes, raw bytes, no normalization.** Phase 3's refactor changes zero published bytes; Task 11's proof
gate is cleared. It needed no manifest and no secrets — the manifest is reconstructible from the live
catalog, because `mediaId(seed) = SHA-256(seed)[0:7]` over ids that `content-1a` already publishes. See
`service/content-pipeline/tools/reconstruct_staging_manifest.py`.

Two traps that cost real time, now fixed in the plan: the old Step 5 diffed
`python3 -m json.tool --sort-keys` on both sides, which **cannot see key order** — the one regression the
step exists to catch (verified: swap `dhd`/`dsd` in one entry and the sorted diff still says identical).
And `sed -n '/^{/,$p'` is wrong because Gradle prints banner lines even under `-q`; use `grep '^{'`.

### Do this first

**Rotate two live secrets** that were pasted into chat: the Cloudflare scoped token (`cfut_…`) and the
BFL API key. Task 5 is blocked until the new Cloudflare token exists.

### Then, in order

1. **Task 1 — `applicationId` → `app.stillscenes`.** Fully automatable — **it does not need the console.**
   The `firebase` CLI is authed and has `apps:create`, `apps:android:sha:create`, and `apps:sdkconfig`
   (verified 2026-07-10); it mutates the Firebase project, so it needs named approval, not a browser.
   `namespace` is already `wallapp.app.android`, so **no Kotlin source moves.**
   `GoogleSignInFactory.kt:13` holds a project-scoped **web** OAuth client id (`client_type: 3`) — it is
   **correct** (verified against `google-services.json`); do not change it.

   **The SHA-1 to register is `65:35:5E:DC:27:99:1A:B4:81:2F:C8:61:36:71:A4:AC:DB:10:97:51`.** Do not
   copy the one the console already shows on `com.example.wallapp` — that one is wrong. See the trap below.

2. ~~**Task 4 — deploy `catalog_version_staging`.**~~ **Done 2026-07-10.** RC is at version 5 with
   `catalog_version_staging = 20260709-06`, verified by reading it back from the API, not from the CLI's
   "Deploy complete". **Trap:** RC caps a parameter `description` at **256 characters** and rejects the
   whole deploy with `DESCRIPTION_EXCEEDS_MAXIMUM_SIZE` — a pre-write validation, so a rejected deploy
   changes nothing. `firebase deploy --only remoteconfig` replaces the *entire* template; check the live
   parameter set is a subset of your local one before deploying, or you silently delete parameters.

3. **Task 5 — prod cache rule. NOT DONE. Tested behaviorally 2026-07-10, after Task 11 gave us real
   objects to fetch.** The custom domain is attached, but the cache rule is **scoped to the staging
   hostname only**:

   | host | `api/…/content-1a` | `media/artist/…/profile.webp` |
   |---|---|---|
   | `media-staging.stillscenes.app` | `HIT` | `HIT` |
   | `media.stillscenes.app` | **`DYNAMIC`** | **`DYNAMIC`** |

   `DYNAMIC` means *not eligible for caching at all* — stronger than `MISS`. Note even the `.webp` is
   `DYNAMIC`, so this is not merely the extensionless-JSON problem. **Every prod request currently bills
   an R2 Class B operation.** It is required, not an optimization.

   The earlier guess that the 2026-07-07 rule matched `media[-staging].stillscenes.app` and thus already
   covered prod was **wrong**. Reading the rule would have taken a token; testing it took two `curl -I`s.
   Fixing it **does** need the rotated Cloudflare token (Cache Rules: Edit) — replicate the staging rule's
   `http_request_cache_settings` entrypoint onto the prod hostname.

4. **Task 11 — PUBLISHED 2026-07-10. Prod is live, with one wrong object.**

   `content-1a`, `spec.json`, all 18 media-map copies and all 11 renditions are correct and verified
   against the CDN. **`content-metadata-1a` is degraded**: empty `styles`/`tags`/`colors`/`searchTerms`
   where staging has `landscape`/`nature`/`alpine`, `amoled`/`dark`/`aurora`, etc. Search by tag returns
   nothing on prod; title suggestions still work. No user is affected — no release build has shipped.

   **Cause, and the lesson.** A manifest produces *three* wire objects. `styles`/`tags`/`colors` reach
   only `content-metadata-1a`, via `SearchBuilder`. A manifest that drops them still yields a
   byte-identical media map *and* a byte-identical `content-1a` — so the Task 10 Step 5 byte-identity
   proof, which diffs only the media map, cannot see the loss. `stage_prod_publish.py` now recovers the
   three lists from the source catalog's `content-metadata-1a` and refuses to run if any are missing.

   **To repair:** re-stage and re-publish the same version. Re-PUT overwrites (write-once is convention,
   not enforcement), and `Publisher` retries read-back past the `immutable` edge cache with `?cacheBust=N`.
   Verify all three objects afterwards, not one:

   ```bash
   for k in content-1a content-metadata-1a spec.json; do
     cmp <(curl -sS "$PROD/api/$V/$k") <(curl -sS "$STAGING/api/$V/$k") && echo "identical $k"
   done   # media-1a-c-p~s should differ from staging ONLY by the host substring
   ```

   Original staging procedure, still correct:

   ```bash
   python3 service/content-pipeline/tools/stage_prod_publish.py /tmp/task11
   ./gradlew :service:content-pipeline:run --args="/tmp/task11/manifest.json --dry-run"   # READ THE BUCKET LINE
   ./gradlew :service:content-pipeline:run --args="/tmp/task11/manifest.json"             # <- the R2 write
   ```

   The staging script rebuilds the manifest from the live staging catalog, points `baseUrl` at
   `https://media.stillscenes.app` (which is what selects the prod bucket), and re-downloads the 11
   `.webp` renditions from staging — `Main.kt:90-96` resolves each as `File(manifestDir, renditionPath)`
   and the prod bucket is empty, so the publish uploads images, not just JSON.

   Verified 2026-07-10, nothing written: dry run reports `bucket stillscenes-content-prod`, 11 media
   entries; the emitted media map is **byte-identical to live staging modulo the host**, with zero
   `media-staging` occurrences; all 5 target keys currently `404`, so the publish creates and never
   overwrites; `wrangler` is OAuth-authed (so this needs **no** rotated Cloudflare token) and
   `media.stillscenes.app` is attached to the prod bucket with active SSL.

   The write is **32 objects**: 11 renditions, 18 media-map copies (2 platforms × 9 size classes),
   `content-metadata-1a`, `spec.json`, and `content-1a` **last** — so a partial upload never activates a
   half-published catalog.

   **`catalog_version` is already `20260709-06`.** The instant `content-1a` lands in prod, release builds
   serve it. No RC flip is needed afterwards, despite what `Main.kt:99`'s success message says.

**Until Task 11 lands, release builds are non-functional** — their in-app default names `20260709-06`,
which the empty prod bucket cannot serve. Safe only because nothing is in anyone's hands. **Do not cut a
release build first.**

---

## Known gaps, not blocking

- **No test isolates the `sdMediaId` shape check.** `CatalogBuilder` always sets `sdMediaId == hdMediaId`,
  so the `hd` check shadows it against the real fixture. The check still guards hand-edited catalogs.
- **Enum-vs-renderer drift is uncovered.** Nothing verifies that a key a `ViewStateFactory` requests is a
  key some `MediaEntityKind` emits. Closing it needs a hand-maintained render-site map — a second source
  of truth, deliberately declined.
- **iOS is unverified.** Everything was driven on the Android emulator.
- **`--dry-run` prints only the media map**, not `content-1a` or the search metadata.
- `Factory.desktop.kt:143` and `Factory.ios.kt:172` hardcode `applicationId = "com.wallapp.example"` — a
  *third* string, wrong today, unrelated to the Task 1 rename.
- One unexplained ANR (`failed to complete startup`, main thread in `handleBindApplication`) on a
  LeakCanary debug build. Never reproduced, never root-caused. Not claimed fixed.
- `WaeTestModule.kt` binds `NetworkMediaMapRepository` to the old network impl, diverging from production.
- `image_host_name` is a dead RC param; `highlight_just_added` is not read by the client.
- The Plus carousel card renders a literal `(placeholder)` — a design-asset gap, not code.
- RC rollback drill never run.
- **`.gitignore` covers none of `node_modules/`, `.wrangler/`, `learn/`**, all untracked. One `git add -A`
  commits a few thousand files. **Always `git add` explicit paths in this repo.**

---

## Machine quirks

- Gradle is pinned to JDK 17 in `~/.gradle/gradle.properties`. `/usr/libexec/java_home -v 17` does
  **not** resolve the Homebrew keg; use `/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home`.
  `./gradlew` works regardless.
- Python 3.14.6 here has a **broken `xml.etree.ElementTree`** (`pyexpat` symbol mismatch). Parse Gradle
  JUnit XML with `grep`/`sed`. `json` is fine.
- `pytest` is not installed and PEP 668 blocks `pip install`; pipeline scripts use stdlib `unittest`.
- Firebase rules tests need IPv4-first DNS (already in the npm test script).
