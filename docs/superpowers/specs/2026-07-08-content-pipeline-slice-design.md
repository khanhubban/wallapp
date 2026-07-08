# Content Pipeline — Thin Vertical Slice (Design Spec)

- **Status:** Draft v2 (research + adversarial review incorporated; pending user sign-off)
- **Date:** 2026-07-08
- **Branch target:** new branch off `main` after `feat/foundation-delivery` merges
- **Relationship to prior work:** consumes the delivery contract from `…/plans/2026-07-02-foundation-delivery-path.md`; realizes the *production* half of `…/specs/2026-07-02-delivery-r2-design.md` + `…-schema-validation-design.md`, scoped to one end-to-end slice.

---

## 1. Goal

Generate a small real set of AI wallpapers and get them rendering in the app **through the existing (untouched) client contract**, with a publish that **physically cannot ship a catalog the client can't render**. Success = one command that produces → validates → publishes 3–5 wallpapers to R2 staging, read-back-verifies over the CDN, after which flipping RC `catalog_version` shows them in the app.

## 2. Scope

**In:** FLUX.2 [klein] generation → local Real-ESRGAN upscale → renditions → build the **demo-format** catalog/search/media via the app's own models → minimal-but-safe validation → publish to R2 **staging** + CDN read-back. Manual curation; manual RC flip.

**Out (later slices — §12):** target `catalog/<version>.json` + content-addressed layout; full validation library (pHash/license/luminance); canary telemetry + abort thresholds; automation/scheduling; prod bucket + promotion; retirement/dedup-across-runs; per-class rendition optimization; seed-catalog bundling.

## 3. Locked decisions

| Decision | Choice |
|---|---|
| Slice shape | Thin vertical slice |
| Wire format | **Match the demo format** (client untouched) |
| Runtime home | **Hybrid** — script (generate+upscale) + Kotlin `service:content-pipeline` (validate→build→publish) |
| Validation depth | **Minimal but safe** (decode + rendition + referential + meta gate; aesthetic/dedup/license deferred) |
| Compute | Hosted FLUX API + local Real-ESRGAN; the 12 GB CPU-only Singapore VPS is **not** in the path |

## 4. Target wire format (verified against `demo-assets/api/99999999/` + client models)

Client fetches, per RC-selected `<version>`, at `{baseUrl}/api/<version>/`:

| File | Model | Notes |
|---|---|---|
| `content-1a` | `NetworkContent` | **Required arrays:** `wallpapers[]`, `categories[]`, `artists[]`, `folders[]`. Each `wallpaper` = one image; carries numeric media IDs |
| `content-metadata-1a` | `NetworkSearchMetadata` | **Required arrays:** `remixMetadata[]`, `artistMetadata[]`, `folderMetadata[]`; remix metadata requires `titleSuggestions` |
| `media-1a-<p>-<b>` | `NetworkMediaData` = `Map<Long, NetworkMediaMap>` | **Keyed by numeric media ID (`Long`)**, not remixId. Value `NetworkMediaMap` = `Map<String,String>` = **`SizedImage.key → url`** |
| `spec.json` | — | **Dead weight for the active client** (the RC repo builds endpoints locally "instead of downloading spec.json"). Produce for parity; do **not** gate publish or assert as contract |
| ~~`key`~~ | — | **Omitted** — verified: no key-fetch anywhere in the plaintext client (legacy AES/GCS artifact) |

**`SizedImage.key` inner keys** (from `SizedImage.kt`): downloads `dhd`/`dsd`; feed/fullscreen `s`/`wfs`/`wft`/`fs`; artist `am`/`as`/`e`; collection layers `wcs0..2`/`wcl0..2`. A media ID's entry must carry the inner keys its role needs, or that surface breaks.

**Media-ID sources** the catalog contributes (all must be keys in the media maps): `wallpaper.dlm.hd`, `wallpaper.dlm.sd`, `wallpaper.previews[].id`, `artist.profileImage.id`, `folder.profileImage.id`, `folder.featureBannerImage.id`, `category.featureBannerImage?.id`.

**Imgix invariant (load-bearing):** the client appends resize/format params **only** to URLs on the imgix host prefix; every other URL is passed through unchanged and downsampled client-side. Our R2 URLs render *because* they are not imgix URLs. → rendition URLs **MUST NOT** start with the imgix host prefix.

Version string is **opaque to the client** → use dated `YYYYMMDD-NN`, set RC to match.

## 5. Architecture & components

### A. Generation script — `script/generate_content` (Python/shell, dev-local)
- Prompt set + **BFL API key from env** (never committed).
- **BFL is async:** POST → poll `get_result` (~500 ms) until `Ready`; **download each sample within its 10-min URL expiry**; capped backoff on 429; `moderated`/`Error`/`Failed` → **skip-and-log, not crash**; **cap total generations/run** (cost guard).
- Curate winners → upscale with **`realesrgan-ncnn-vulkan`** (Apple-Silicon GPU) using **`RealESRGAN_x2plus`** (native 2×) — *not* the PyTorch path (CPU fallback ~100× slower); avoid tiny tiles. **Eyeball a dark-gradient sample on real OLED** (banding; luminance gate deferred).
- Emit `masters/<id>/upscaled.png` + `meta.yaml` (id, `source`, `license`, prompt, seed, theme). **Upload `masters/` + `meta.yaml` to the `stillscenes-masters` bucket** (foundation Task 4) — the single non-derivable input the DR story rests on.
- **Boundary:** no knowledge of app models or R2 layout. Reuses the *structure* of `demo-assets/gen_wallpapers.py`, swapping procedural rendering for FLUX+upscale.

### B. Kotlin module — `service:content-pipeline` (`kotlin("jvm")` Gradle module)
Added to `settings.gradle.kts` beside `service:service-common`. Depends on `content-network`, `mediamap-network`, **`search-model`** (not `search-network`) — all expose `desktop()`/JVM targets and `service:service-common` already depends on the first two and compiles, so this is proven viable. (Accept that `content-network` transitively pulls ktor + firebase into the JVM classpath — already true for `service-common`.) Stages compose into `./gradlew :service:content-pipeline:publish -Pversion=<YYYYMMDD-NN>`:

1. **RENDER** — produce a **minimal rendition set** per wallpaper (e.g. a full-res download image + a smaller preview), each **fit within** the relevant `ImageBucketSpec` *maxima* (`maxWidthPx/maxHeightPx` are ceilings, **not** exact targets) with **aspect preserved**; `f~fo` is **landscape** (needs its own composition, not a portrait squeeze). Per-class renditions are a *bandwidth* optimization, not a display requirement (client downsamples non-imgix URLs) → **deferred to [LATER]**; the slice mirrors the demo, whose 18 media files are identical. Encode WebP at **q90+/near-lossless + dithering** (8-bit banding on dark OLED), opaque. **Strip** all EXIF/XMP/PNG-text; compute `w/h/bytes` (+ additive `avgColor`/`blurHash`).
2. **BUILD** — construct a valid `NetworkContent` (§6) + `NetworkSearchMetadata` + one media-ID→`{SizedImage.key→url}` map, and serialize `content-1a`, `content-metadata-1a`, 18× `media-1a-<p>-<b>` (identical across `p`/`b`, demo-faithful), `spec.json` — using the app's own serializers.
3. **VALIDATE** — fail-fast gates:
   - **Pre-BUILD meta gate:** required fields present, `id == dirname`, ids globally unique, `category ∈ committed enum`.
   - **Post-BUILD:** (a) decode round-trip of all three files through the app's current models — *necessary, not sufficient* (the lenient `Json` swallows unknown keys / coerces); (b) rendition re-decode — opens, dims **≤ class maxima** + aspect sane, **zero EXIF/XMP/PNG-text**, no URL on the imgix prefix; (c) **referential integrity, corrected:** every `Long` media ID referenced by `content-1a` (dlm.hd/sd, previews[].id, profileImage/featureBannerImage ids) is a **key in each media file**, with the **required inner `SizedImage.key`s present** per role; and the **`remixId`/wallpaper-id set matches between `content-1a` and `content-metadata-1a`** (that equality is valid; content↔media equality is *not* — different ID spaces).
4. **PUBLISH** — `<version>` **write-once** (never re-PUT an existing path; immutable 1-yr cache + relaxed CDN consistency → a re-run can pin stale bytes; bump `-NN` instead). **No in-repo R2 PUT exists** (the only export path is Firebase/GCS + AES — not reusable), so this is net-new: PUT via **`rclone`** invoked with `ProcessBuilder` (remote/creds from the dev Mac's rclone config); prove a real PUT+GET in CI. Strict order: renditions → media/search → **`content-1a` last**. **Read-back over the CDN:** HTTP 200 + byte-hash + decode with **current** models; accept `cf-cache-status ∈ {MISS,EXPIRED,DYNAMIC}` (first fetch is MISS→origin, strongly consistent; `HIT` would false-fail). Mismatch **aborts, does not flip RC**.

### C. Client — untouched
Operator flips RC `catalog_version`; app fetches `api/<version>/…` and renders (foundation Task 10 drill).

## 6. Content-model mapping (the substantive work — corrected)

Build a minimal but **fully-required-field-complete** instance. The impl plan pins every field/enum by reading the live models; the shape:

- **`artists[]`** — ≥1 synthetic artist (`stillscenes`); required `profileImage` (a real `Long` media ID present in the maps); `socialLinks` → project/empty.
- **`categories[]`** — ≥1 category; **every `wallpaper.categoryId` must resolve** to one; optional `featureBannerImage` (media ID if present).
- **`folders[]`** — ≥1 `Just Added`-style folder; required `profileImage` **and** `featureBannerImage` (both media IDs).
- **`wallpapers[]`** — one per image; required `id, label, collectionLabel, type, artistId, dlm{hd,sd}, isDark, categoryId, isSingle, previews[]{id,…}, slugs`. Contributes media IDs via `dlm` + `previews[].id`.
- **`content-metadata-1a`** — `remixMetadata[]` (one per wallpaper; requires `titleSuggestions`, weighted `styles/tags/colors`, `searchTerms`), `artistMetadata[]` (per artist), `folderMetadata[]` (per folder). Tags/colors from prompt theme + computed `avgColor` (minimal deterministic derivation; aesthetic tagging deferred).
- **Media maps** — one `Long id → {SizedImage.key→url}` entry per media ID above, carrying the inner keys its role needs (downloads `dhd/dsd`; previews `s/wfs/wft/fs`; artist/folder images `am/as/e` / appropriate keys), pointing at the minimal rendition set.

## 7. Error handling & idempotency
- **Script:** BFL/upscale failures → retry-then-skip-and-log; manual curation ⇒ no partial *published* state.
- **Module:** VALIDATE blocks all uploads on failure; PUBLISH strict-order + read-back-verified, leaves RC on prior version on any failure.
- **Idempotency = write-once versioning**, not re-PUT (immutable cache + relaxed CDN consistency); retries bump `-NN`.
- **Carry-forward neutralized:** read-back decode catches an HTTP-200-bad-body the client would silently swallow into empty.

## 8. Testing (TDD)
Golden fixtures are **structural**, not byte-level (demo `content-1a` is 200 wallpapers on the googleapis host; all 18 media files are byte-identical stubs — byte-equality is impossible). Assert: schema/field-presence/shape parity vs `demo-assets/api/99999999/*` + round-trip through the client models. Unit tests: RENDER (rendition set, dims ≤ max + aspect, stripped metadata, non-imgix URLs, WebP quality), BUILD (all required arrays/fields; media-ID keying; inner keys), VALIDATE (rejects: missing required field, dangling media-ID, missing inner key, wrong dims, leaked EXIF, imgix-prefixed URL, bad meta, content↔search id mismatch), PUBLISH (strict order + calibrated read-back vs a fake endpoint). Script: smoke test w/ mocked BFL.

## 9. Exit criteria
1. `publish -Pversion=<v>` produces + validates + publishes 3–5 wallpapers to R2 **staging**.
2. CDN read-back passes (200 + byte-hash + decode) for every object.
3. Flipping RC to `<v>` shows the wallpapers in the app; reverting restores prior content.

## 10. Dependencies
- **Cloudflare domain** `media-staging.stillscenes.app` on the R2 staging bucket — **same operator gate as foundation Task 10** (pipeline is buildable + unit-testable without it; only the final publish→read-back→device step needs it).
- BFL API key; R2 staging scoped token + `stillscenes-masters` bucket (foundation Task 4); `realesrgan-ncnn-vulkan` + local `rclone` config on the Mac.

## 11. Defaults
All 18 media files (identical, demo-faithful); 3–5 wallpapers; dark/OLED prompts; BFL key via env; version `YYYYMMDD-NN`.

## 12. Deferred to later slices (lifecycle backlog)
1. Format migration to `catalog/<version>.json` + content-addressing.
2. **Canary telemetry as the gate for the first *prod* flip** — `catalog_decode_failed{version}` / `catalog_sync_result{version,outcome}` + abort thresholds must exist before any *prod* RC flip (client-side → later slice) *(delivery §12.4)*.
3. Retirement/archival + ~1,500 catalog cap (ship-plan §3.3).
4. Cross-run pHash dedup.
5. Prod promotion path (prod bucket + prod token + prod RC).
6. schemaVersion evolution + shaded-runtime frozen jar (FORWARD_TRANSITIVE) — when app versions diverge; today current-model decode suffices.
7. Full validation library (pHash / license allow-deny / OLED-luminance).
8. **Per-class rendition optimization** (true per-bucket sizes for bandwidth) — correctness doesn't require it (client downsamples).
9. Observability: machine-readable validation report + per-stage logs + non-zero exit.
10. Cost governance + token blast radius (prod); no R2 object versioning → masters snapshot + bucket-scoped CI tokens first.
11. Rights/licensing: BFL Service Terms grant BFL a license-back; USCO — prompt-only AI has no US copyright; **confirm the BFL Developer-ToS commercial-use clause once**; moat is brand/curation. (klein 4B Apache-2.0 / 9B non-commercial is moot on the hosted API.)
12. `avgColor`/`blurHash` computed but the current client ignores them — keep additive, don't gate.

## 13. Review & research provenance
- **Adversarial review (v1→v2):** fixed B1 (media maps keyed by numeric `Long` media IDs, not remixId), B2 (`{SizedImage.key→url}` inner maps, not one URL), B3 (all required arrays/fields incl. `titleSuggestions`, artist/folder images), H1 (`ImageBucketSpec` maxima not exact dims; `f~fo` landscape), H2 (imgix pass-through invariant; per-class renditions = bandwidth → deferred), M1 (structural not byte-level fixtures), M2 (decode round-trip necessary-not-sufficient), M3 (`spec.json` dead weight), L1 (rclone PUT via ProcessBuilder; no in-repo R2 precedent), L3 (`search-model` not `search-network`). **Verified correct:** 9+2 keys, `key`-omission safety, contract accuracy, JVM-module→models feasibility.
- **To confirm (non-blocking):** FLUX.2 [klein] max-MP; BFL Developer-ToS commercial-use clause.
- **Research pass:** external gotchas (BFL async/expiry/moderation, Real-ESRGAN ncnn-vulkan, WebP banding, R2/CDN consistency) folded into §5; lifecycle → §12.
