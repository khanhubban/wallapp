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
| Remote Config | version 4 |
| `catalog_version` | `20260709-06` — **release builds read this** |
| `catalog_version_staging` | **not yet deployed** (Task 4). Clients fall back to the compiled-in default, which is correct. |
| CDN, debug builds | `media-staging.stillscenes.app` → R2 `stillscenes-content-staging` — healthy, `HTTP 200` |
| CDN, release builds | `media.stillscenes.app` → R2 `stillscenes-content-prod` — **bucket empty**, `HTTP 404` |
| `applicationId` | still `com.example.wallapp` (Task 1) |

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
adb shell am start -n com.example.wallapp/wallapp.activity.MainActivity   # becomes app.stillscenes after Task 1
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

1. **Task 1 — `applicationId` → `app.stillscenes`.** Steps 1-3 and 7-11 are automatable; **Step 4 needs
   the Firebase console**: register the app, add the debug keystore's SHA-1 (`app/android/debug.keystore`,
   alias `androiddebugkey`, password `android`), download the new `google-services.json`.
   `namespace` is already `wallapp.app.android`, so **no Kotlin source moves.**
   `GoogleSignInFactory.kt:13` holds a project-scoped **web** OAuth client id (`client_type: 3`) — do
   **not** change it. A `DEVELOPER_ERROR` / status 10 means the SHA-1 is wrong, not the client id.

2. **Task 4 — deploy `catalog_version_staging`.** Add it to `firebase-backend/remoteconfig.template.json`
   (value `20260709-06`), then `firebase deploy --only remoteconfig -P stillscenes-prod`.

3. **Task 5 — prod cache rule.** The custom domain is already attached. **Do not read the rule; test it.**
   The prod host's current `404` proves nothing either way — Cloudflare does not cache 404s. The moment
   Task 11 puts the first object in the prod bucket, fetch it twice and watch `cf-cache-status` go
   `MISS` → `HIT`. If the 2026-07-07 rule matched `media[-staging].stillscenes.app`, Task 5 is already
   done and needs no rotated token. If the second fetch still says `MISS`, add the extensionless-`/api/`
   cache rule. It is **required, not an optimization**: our catalog objects are extensionless and
   Cloudflare does not cache JSON/HTML by default, so without it every request bills an R2 Class B op.

4. **Task 11 — the first prod publish.** Needs the real manifest **directory** — the `.webp` renditions
   must sit on disk beside the manifest, because `Main.kt:90-96` resolves each rendition as
   `File(manifestDir, renditionPath)` and the prod bucket is empty, so this uploads the images too. A
   reconstructed manifest cannot publish. Publish with `baseUrl: https://media.stillscenes.app` (which is
   what selects the prod bucket), then verify a release build renders.

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
