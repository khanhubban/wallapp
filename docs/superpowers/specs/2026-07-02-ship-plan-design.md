# Ship Plan — Forking WallApp into a Production Wallpaper App

**Date:** 2026-07-02
**Status:** Approved section-by-section in brainstorming; pending final review.
**Companion research:** [research/2026-07-02-report-engineering.md](research/2026-07-02-report-engineering.md), [research/2026-07-02-report-aigen.md](research/2026-07-02-report-aigen.md)

---

## 1. Goal and context

Ship a rebranded, production wallpaper app on **Android and iOS**, built from this repo (the open-source release of Panels). The client code is essentially complete; what's missing is everything the OSS release omitted: the content backend, real service accounts, branding, legal, and release operations.

**Decisions made during brainstorming:**

| Decision | Choice |
|---|---|
| Goal | Ship own app (not just learn) |
| Platforms | Android + iOS together (Apple account already active) |
| Content source | AI-generated + licensed photo sources, mixed |
| Content ops | Weekly drops; ~100–300 wallpapers at launch |
| Monetization | Ads + "Plus" subscription, exactly as built (AdMob + RevenueCat) |
| Backend approach | **A: script-driven pipeline** (vs. CMS or custom server) — git-managed content + Kotlin export tool → Firebase Storage |
| Generation | **Hosted FLUX API** (~$0.015/image) + local Real-ESRGAN upscale. Self-hosting ruled out: the 12 GB CPU-only VPS cannot run FLUX (needs ~30 GB RAM CPU-only or 8+ GB GPU VRAM); at 10–50 images/week the API costs ~$1–4/month |

**Key architectural fact this plan preserves:** the app has **no live API server**. Clients download versioned static JSON catalogs + image renditions from Firebase Storage and cache them. "The backend" is a publishing pipeline, not a running service — cheap to operate, hard to break.

**Non-goals (v1):** admin web UI, headless CMS, dedicated image CDN, AVIF renditions, pagination rework, Kotlin/Wasm, desktop release. The source-of-truth → export boundary is kept clean so a CMS can replace the filesystem later without touching the export/upload half.

---

## 2. Workstreams and build order

1. **Foundation** — own Firebase project; register both apps; deploy existing Firestore/Storage rules; enable Auth providers, Remote Config, FCM; swap `google-services.json` / `GoogleService-Info.plist`; replace `panels-oss` project references. *Verification: app runs end-to-end against own Firebase with bundled demo content.*
2. **Content pipeline** (the engineering core — §3). *Verification: app loads our catalog from our Storage instead of bundled data.*
3. **Monetization wiring** (§5) — parallel after 1.
4. **Rebrand** (§5) — parallel after 1.
5. **Legal + store presence** (§5) — parallel after 1.
6. **Release** (§5) — last.

---

## 3. Content pipeline

### 3.1 Source of truth — content workspace

Private git repo (separate from the app repo):

```
content/
  candidates/               # raw generation output awaiting curation
  wallpapers/<id>/
    master.png              # high-res original (AI output or licensed photo)
    meta.yaml               # title, category, tags
                            # source: ai | photo-licensed | own   (MANDATORY)
                            # license: model/provider or photo license + attribution (MANDATORY)
                            # tier: free | reward-unlock | plus-only
  collections/
    YYYY-MM-drop-NN.yaml    # weekly drop: wallpaper ids, ordering, featured flags
```

`source` + `license` are validation-enforced from day one — the audit trail for store review and takedowns. AI and photo content flow identically; the pipeline is source-agnostic.

### 3.2 Pipeline stages — `service/service-content-pipeline` (new Kotlin module)

Reuses `service-common` + `RemoteApiStorageManager`. One command runs:

- **Stage 0 — Generate** (helper, outside the publish path): FLUX.2 [klein] hosted API, prompt templates + seed control for house style → `candidates/`. Curation ≈ 5:1 promotes winners to `wallpapers/`. **Real-ESRGAN upscale locally** to 1440×3200 / 4K / tablet sizes (mandatory: hosted models cap ~4 MP, below wallpaper resolution). Launch ≈ $23; steady state ≈ $1–4/month.
- **Stage 1 — Validate:** schema-check every `meta.yaml`; masters exist with min resolution/aspect; license present; **referential check** — every media URL in the candidate catalog resolves to an object that will exist in the version directory. Fails fast; nothing uploads.
- **Stage 2 — Render:** per-device-class renditions the app already expects (the `media-1a-*` classes visible in `demo-assets/api/99999999/`), WebP baseline, replicating the demo format exactly in v1. (AVIF dual-publish gated on iOS 16+/Android 12+ is phase 2.)
- **Stage 3 — Build:** serialize catalog JSON **using the app's own `shared/data/content-network` models** into a fresh `api/YYYYMMDD/` directory. Contract rules enforced in CI: additive-only schema changes (every new field has a default; no removals/retypes), and a **decode gate** — the candidate catalog must decode with frozen serializer models from the oldest supported app release (FORWARD_TRANSITIVE compatibility).
- **Stage 4 — Publish**, strict order: upload all renditions → upload immutable catalog JSON → fetch everything back through the CDN and re-validate → flip the **canary pointer** (read by a hashed 5% of installs) → bake ≥24h against decode-failure/crash metrics → flip the main pointer. Exact `key` mechanics are verified against client code during implementation.

### 3.3 Storage, caching, rollback

- Versioned objects (`api/YYYYMMDD/**`) are **immutable**: `Cache-Control: public, max-age=31536000, immutable`. Pointer objects: `no-cache` + ETag — steady-state client refresh is a ~300-byte 304.
- Rollback = re-point to N−1. Retain N−2 and older while any pointer or installed client may reference them. Content removal is two-phase (drop from catalog first; delete objects only after old catalogs age out) so an old catalog never references a missing asset.

### 3.4 Client models & serialization

Shared KMP `Json { ignoreUnknownKeys = true; coerceInputValues = true }`; every non-essential catalog model property has a default. Audit existing models for compliance; nullable-without-default catalog fields are a review blocker. No `@ExperimentalSerializationApi` in catalog models.

### 3.5 Testing

- Unit tests: validation rules; JSON round-trip (pipeline serialize → client deserialize → equality).
- Contract test: oldest-client frozen-model decode gate (runs in pipeline CI).
- E2E: staging publish (existing `RemoteApiExportSpecStaging` path) consumed by a debug build before any prod publish.

---

## 4. Client resilience contract

The app already has a production networking layer; implementation starts with an **audit mapping existing behavior against this contract**, patching gaps only.

1. **Sync state machine:** `Idle → CheckingPointer → Downloading(version) → Validating → Committed | Failed(retryAt)`. The cached catalog is the only thing UI reads; sync advances it atomically version-to-version. A bundled seed catalog ships in the binary (offline first launch).
2. **Retry policy (written, pre-launch):** retry only network errors + 5xx, never 4xx; capped exponential backoff with full jitter (1s base, ×2, 60s cap, max 5 attempts); retries at exactly one layer (repository). Timeouts: ~10s connect, ~30s read for catalog JSON; media via platform download managers.
3. **Jitter every schedule:** no fixed-clock fetches (whole install base hits one pointer file). Android: WorkManager periodic with randomized offset. iOS: self-rescheduling `BGAppRefreshTask` with jittered `earliestBeginDate` (identifiers declared in Info.plist from day one). Foreground refresh only when cache older than 12h, jittered.
4. **Connectivity = deferral signal,** not a pre-flight gate: failed work queues until connectivity returns (`NetworkType.CONNECTED` / `NWPathMonitor`).
5. **Graceful degradation (launch-checklist item):** with Storage unreachable, browsing/search/set-wallpaper work from cache indefinitely; only new content stops.

---

## 5. Monetization, rebrand, legal, release

### 5.1 Monetization wiring

- **AdMob:** account + both app entries + ad units per format (native feed, native video, reward, interstitial, app-open). Real IDs in the `AdUnitIds` implementations (`shared/app/app-adapter`, iOS Swift side) — **release builds only; debug keeps Google test IDs**. UMP privacy message configured in console. Boot contract: `requestConsentInfoUpdate()` every launch → `canRequestAds()` gate → SDK init → load ads; audit existing `PrivacyMessagingManager` code against it. Native-ad pool discipline (sized-to-need, ~1h TTL, `destroy()` on eviction) audited likewise. *(These integration claims are doc-sourced but were not adversarially verified — spot-check current Google docs during implementation.)*
- **RevenueCat:** project + API keys; single entitlement (`plus`) with monthly/annual products attached, mirrored in Play Console + App Store Connect. Release-checklist line item: "every product is attached to the entitlement" (documented pay-but-locked failure mode). Existing `license-state` gating unchanged. Test procedures per `doc/ads.md` / `doc/billing.md`.
- **iOS ATT** prompt with honest copy (AdMob uses the advertising identifier).

### 5.2 Rebrand

Name (store + trademark + domain check) → new `applicationId`/bundle ID, icons, splash, in-app copy, URL schemes (deep links + Google Sign-In), store metadata. Internal `wallapp` package names stay (Apache-2.0 permits; only user-visible branding and all "Panels" references must change). Grep-driven inventory (`Panels`, `panels-oss`, visible `WallApp` strings) in the implementation plan. Lottie asset licenses reviewed.

### 5.3 Legal + store presence

Privacy policy + ToS on own domain disclosing the exact collector list (Firebase Auth/Firestore, AdMob, RevenueCat, Crashlytics), linked in-app and in both listings. Play data-safety form and App Store privacy labels consistent with that list. **Open item:** verify Google Play's current AI-generated-content policy against the primary source before launch (research left this unverified).

### 5.4 Release

Android keystore + Play App Signing; iOS signing under existing account. Internal tracks first (Play internal testing + TestFlight) including a **real test purchase visible in RevenueCat**. Staged rollout (Play percentage, iOS phased release). Crashlytics dSYM upload and Android baseline profile verified. A written **launch checklist** is the ship-day contract (incident-derived additions over time), seeded with: repeatable publish builds + canary + staged rollout; written timeout/retry policy per call path; graceful-degradation verification; "products attached to entitlement".

---

## 6. Error handling summary

| Failure | Behavior |
|---|---|
| Pipeline validation fails | Run aborts before any upload |
| Upload interrupted | Live catalog untouched (clients only follow pointer); re-run |
| Bad catalog reaches canary | Canary metrics catch it; main pointer never flips; re-point canary |
| Bad catalog fully live | Re-point to N−1 (retained) |
| Client fetch fails | Backoff+jitter retries (network/5xx only), then `Failed(retryAt)`; UI serves cache |
| Storage outage | App fully functional on cache; sync resumes on recovery |
| Consent unavailable / ads fail | App works ad-free; no retry loops on ad load failure |
| RevenueCat unreachable | Cached CustomerInfo + offline entitlement fallback |

---

## 7. Research provenance

Two research passes (adversarial verification interrupted early to cap token cost; findings tagged):
- **Engineering:** confirmed — SRE launch checklist/retry/jitter/staged rollout, Confluent compatibility semantics, kotlinx.serialization evolution mechanics, Etsy atomic pointer-flip. Unverified — offline-first specifics, background-scheduling specifics, image delivery, all AdMob/RevenueCat items (spot-check during implementation).
- **AI generation:** confirmed — FLUX pricing ballpark, FLUX.1-schnell Apache-2.0 (commercial-safe), FLUX.1-dev weights non-commercial (outputs via licensed APIs OK), SD 3.5 commercial < $1M/yr, USCO Jan-2025: prompt-only AI images have no US copyright (moat = curation/brand/freshness, not image IP), hosted models cap ~4 MP (upscale stage is structural). Unverified — Leonardo terms, GPU-rental spot rates, Play AI-content policy.
