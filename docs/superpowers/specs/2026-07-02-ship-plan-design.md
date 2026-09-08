# Ship Plan — Forking WallApp into a Production Wallpaper App

**Date:** 2026-07-02
**Status:** v2 — external-review deltas applied inline (2026-07-02). This document is canonical; companions are design-rationale records.
**Companion research:** [research/2026-07-02-report-engineering.md](research/2026-07-02-report-engineering.md), [research/2026-07-02-report-aigen.md](research/2026-07-02-report-aigen.md)
**Design-rationale records (deltas applied inline here):** [2026-07-02-delivery-r2-design.md](2026-07-02-delivery-r2-design.md) (R2 + Cloudflare delivery, Remote Config pointer, decision history), [2026-07-02-schema-validation-design.md](2026-07-02-schema-validation-design.md) (meta.yaml schema, catalog contract, validation rules)

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
| Backend approach | **A: script-driven pipeline** (vs. CMS or custom server) — git-managed content + Kotlin export tool → object storage + CDN |
| Delivery | **Single R2 bucket + Cloudflare CDN** on `media.<domain>`; **Firebase Remote Config `catalogVersion`** as version pointer (percentage condition = canary, edit = rollback). Firebase Storage not used for content. See delivery design |
| Generation | **Hosted FLUX API** (~$0.015/image) + local Real-ESRGAN upscale. Self-hosting ruled out: the 12 GB CPU-only VPS cannot run FLUX (needs ~30 GB RAM CPU-only or 8+ GB GPU VRAM); at 10–50 images/week the API costs ~$1–4/month |

**Key architectural fact this plan preserves:** the app has **no live API server**. Clients download versioned static JSON catalogs + image renditions from an R2 bucket behind Cloudflare's CDN and cache them. "The backend" is a publishing pipeline, not a running service — cheap to operate, hard to break, and egress is structurally $0 at any install count.

**Non-goals (v1):** admin web UI, headless CMS, server-side search, AVIF renditions, pagination rework, Kotlin/Wasm, desktop release, URL signing for premium renditions. The source-of-truth → export boundary is kept clean so a CMS can replace the filesystem later without touching the export/upload half.

---

## 2. Workstreams and build order

1. **Foundation** — own Firebase project; register both apps; deploy existing Firestore/Storage rules; enable Auth providers, Remote Config, FCM; swap `google-services.json` / `GoogleService-Info.plist`; replace `panels-oss` project references. Plus Cloudflare: zone setup, `media.<domain>` custom domain, prod/staging R2 buckets, bucket-scoped tokens in CI secrets, never-challenge WAF rule (delivery design §5). *Verification: app runs end-to-end against own Firebase with bundled demo content.*
2. **Content pipeline** (the engineering core — §3). *Verification: app loads our catalog from our R2 bucket instead of bundled data.*
3. **Monetization wiring** (§5) — parallel after 1.
4. **Rebrand** (§5) — parallel after 1.
5. **Legal + store presence** (§5) — parallel after 1.
6. **Release** (§5) — last.

---

## 3. Content pipeline

### 3.1 Source of truth — content workspace

Private git repo (separate from the app repo):

```
content/                        # git repo: metadata + collections ONLY — no image bytes
  candidates/                   # raw generation output, no meta.yaml (local/scratch)
  wallpapers/<id>/
    meta.yaml                   # structured schema — see schema-validation design §2:
                                # id (== dirname), title, category (∈ committed enum), tags,
                                # tier: free | reward-unlock | plus-only
                                # source: ai | photo-licensed | own   (MANDATORY)
                                # license: {type ∈ closed allowlist, detail, agreement_ref}  (MANDATORY)
                                # ai: {model ∈ allowlist, provider, seed, prompt_ref}  (if source: ai)
                                # status: staged | published | retired  +  retired_at
  collections/
    YYYY-MM-drop-NN.yaml        # weekly drop: wallpaper ids, ordering, featured flags

r2://<app>-masters              # master images keyed by wallpaper id (H2: multi-GB masters
                                # do not live in git; validated by HEAD check)
```

`source` + `license` are validation-enforced from day one — the audit trail for store review and takedowns. AI and photo content flow identically; the pipeline is source-agnostic. State has one source of truth — the `status` field (`candidates/` merely holds raw output that hasn't been promoted). **Masters durability is load-bearing:** disaster recovery assumes masters + git + deterministic pipeline can rebuild the content bucket, so the monthly `rclone` snapshot of the masters bucket to a secondary (B2/GCS coldline) is **required**, not optional.

### 3.2 Pipeline stages — `service/service-content-pipeline` (new Kotlin module)

Reuses `service-common` + `RemoteApiStorageManager`. One command runs:

- **Stage 0 — Generate** (helper, outside the publish path): FLUX.2 [klein] hosted API, prompt templates + seed control for house style → `candidates/`. Curation ≈ 5:1 promotes winners to `wallpapers/`. **Real-ESRGAN upscale locally** to 1440×3200 / 4K / tablet sizes (mandatory: hosted models cap ~4 MP, below wallpaper resolution). Generate at 2–4 MP and upscale 2× rather than 4× from 1 MP; evaluate Real-ESRGAN on OLED-dark gradients, where banding/oversmoothing shows most. Launch ≈ $23; steady state ≈ $1–4/month.
- **Stage 1 — Validate:** one rule library, run pre-render (content-repo PR CI) and pre-publish (full chain). Metadata schema + license denylist/allowlist, master image checks, rendition re-decode/pHash-dedupe/size-floor checks, catalog decode gates, **referential check** — every media URL resolves to an object staged in this publish or already live. Full rule set: schema-validation design §4. Fails fast; nothing uploads.
- **Stage 2 — Render:** per-device-class renditions the app already expects (the `media-1a-*` classes visible in `demo-assets/api/99999999/`), WebP baseline, replicating the demo format exactly in v1. Compute `avgColor`/`blurhash` here as additive catalog fields. (AVIF dual-publish gated on iOS 16+/Android 12+ is phase 2.)
- **Stage 3 — Build:** serialize catalog JSON **using the app's own `shared/data/content-network` models** into a fresh `api/YYYYMMDD/` directory. Contract rules enforced in CI: additive-only schema changes (every new field has a default; no removals/retypes), and a **decode gate** — the candidate catalog must decode with frozen serializer models from the oldest supported app release (FORWARD_TRANSITIVE compatibility).
- **Stage 4 — Publish** (delivery design §8), strict order, CI-only: PUT all new renditions via S3 API to R2 (headers at PUT time) → PUT `catalog/<version>.json` → **read-back validation through the custom domain** (HTTP 200, byte-hash equality, `cf-cache-status` present, frozen-model decode) → flip the **Remote Config canary condition** to `<version>` @ 5% → bake ≥24h against decode-failure/sync/crash telemetry → promote the RC default condition. Abort at any point = repoint RC to N−1.

### 3.3 Storage, caching, rollback (delivery design §4, §6, §7, §9)

- Single R2 bucket per environment behind Cloudflare CDN on `media.<domain>`. Layout: `media/<wallpaperId>/<hash8>-<class>.webp` (content-addressed, stable across drops — warm client caches survive every flip) + `catalog/<version>.json` (version = `YYYYMMDD-NN`). All objects **immutable**: `Cache-Control: public, max-age=31536000, immutable`. The catalog carries **absolute rendition URLs** (exit hedge: host migration = rclone sync + one catalog publish).
- The version pointer is the RC param `catalogVersion` — no mutable pointer object in v1. Rollback = RC edit to N−1 (propagation bounded by the 12h jittered fetch interval). Retain N−2 and older while referenced. Content removal/takedown is two-phase (drop from catalog → age out → prefix delete + per-URL purge); "aged out" is computable — absent from every retained catalog version and `retired_at` past the client cache window. Never purge for updates.
- **Catalog growth (no client pagination):** the active catalog is capped (~1,500 entries); as drops land, the oldest non-featured wallpapers rotate to `retired`, feeding the two-phase deletion flow. Weekly drops without an archival policy would grow the catalog unboundedly.

### 3.4 Client models & serialization

Shared KMP `Json { ignoreUnknownKeys = true; coerceInputValues = true }`; every non-essential catalog model property has a default. Audit existing models for compliance; nullable-without-default catalog fields are a review blocker — and so are **enum fields without defaults** (the trap `ignoreUnknownKeys` doesn't cover: a new enum value bricks old clients unless `coerceInputValues` has a default to fall back to). Catalog top level carries `schemaVersion`; clients implement **freeze behavior from day one**: if `schemaVersion > maxSupported`, keep serving the cached catalog and surface "update app" (pairs with the RC `minSupportedAppVersion` kill switch). No `@ExperimentalSerializationApi` in catalog models. Full contract: schema-validation design §3.

### 3.5 Testing

- Unit tests: validation rules; JSON round-trip (pipeline serialize → client deserialize → equality).
- Contract test: oldest-client frozen-model decode gate (runs in pipeline CI).
- E2E: staging publish (existing `RemoteApiExportSpecStaging` path) consumed by a debug build before any prod publish.

---

## 4. Client resilience contract

The app already has a production networking layer; implementation starts with an **audit mapping existing behavior against this contract**, patching gaps only.

1. **Sync state machine:** `Idle → CheckingPointer → Downloading(version) → Validating → Committed | Failed(retryAt)`. The cached catalog is the only thing UI reads; sync advances it atomically version-to-version. A bundled seed catalog ships in the binary (offline first launch).
2. **Retry policy (written, pre-launch):** retry only network errors + 5xx, never 4xx; capped exponential backoff with full jitter (1s base, ×2, 60s cap, max 5 attempts); retries at exactly one layer (repository). Timeouts: ~10s connect, ~30s read for catalog JSON; media via platform download managers.
3. **Jitter every schedule:** no fixed-clock fetches (whole install base follows one version signal). Android: WorkManager periodic with randomized offset. iOS: self-rescheduling `BGAppRefreshTask` with jittered `earliestBeginDate` (identifiers declared in Info.plist from day one). Foreground refresh only when cache older than 12h, jittered (matches RC `minimumFetchInterval`). Steady state: one SDK-managed RC fetch; the catalog downloads only when `catalogVersion` changes.
4. **Connectivity = deferral signal,** not a pre-flight gate: failed work queues until connectivity returns (`NetworkType.CONNECTED` / `NWPathMonitor`).
5. **Graceful degradation (launch-checklist item):** with R2/Cloudflare unreachable, browsing/search/set-wallpaper work from cache indefinitely; only new content stops.

---

## 5. Monetization, rebrand, legal, release

### 5.1 Monetization wiring

- **AdMob:** account + both app entries + ad units per format (native feed, native video, reward, interstitial, app-open). Real IDs in the `AdUnitIds` implementations (`shared/app/app-adapter`, iOS Swift side) — **release builds only; debug keeps Google test IDs**. UMP privacy message configured in console. Boot contract: `requestConsentInfoUpdate()` every launch → `canRequestAds()` gate → SDK init → load ads; audit existing `PrivacyMessagingManager` code against it. Native-ad pool discipline (sized-to-need, ~1h TTL, `destroy()` on eviction) audited likewise. *(These integration claims are doc-sourced but were not adversarially verified — spot-check current Google docs during implementation.)*
- **RevenueCat:** project + API keys; single entitlement (`plus`) with monthly/annual products attached, mirrored in Play Console + App Store Connect. Release-checklist line item: "every product is attached to the entitlement" (documented pay-but-locked failure mode). Existing `license-state` gating unchanged. Test procedures per `doc/ads.md` / `doc/billing.md`.
- **iOS ATT** prompt with honest copy (AdMob uses the advertising identifier), sequenced through the UMP explainer flow rather than shown cold.
- **SDK init off the critical path:** MobileAds initialization can hang when offline — initialize asynchronously, never blocking first frame or catalog sync.

### 5.2 Rebrand

Name (store + trademark + domain check) → new `applicationId`/bundle ID, icons, splash, in-app copy, URL schemes (deep links + Google Sign-In), store metadata. Internal `wallapp` package names stay (Apache-2.0 permits; only user-visible branding and all "Panels" references must change). Grep-driven inventory (`Panels`, `panels-oss`, visible `WallApp` strings) in the implementation plan. Lottie asset licenses reviewed. **Apache-2.0 compliance (H4):** retain upstream LICENSE + NOTICE, and keep the in-app OSS-licenses screen (`OssLicenses.kt`) accurate for dependencies we add or remove.

### 5.3 Legal + store presence

Privacy policy + ToS on own domain — the domain's DNS lives on the Cloudflare zone (pages via Cloudflare Pages or elsewhere) — disclosing the exact collector list (Firebase Auth/Firestore, AdMob, RevenueCat, Crashlytics), linked in-app and in both listings. Play data-safety form and App Store privacy labels consistent with that list. **Store listing:** iOS review risk under guidelines 4.2/4.3 is real for wallpaper apps — lean on collections, Plus, and live features to demonstrate differentiation. **AI policy (resolved):** Play's AI-generated-content policy targets apps that *generate* content in-app; a distribution-only catalog is out of its scope — but general content policies still apply to the wallpapers themselves.

**Account deletion (approval blocker — C4):** Firebase Auth = account creation, so Play requires an in-app deletion path **plus** a web deletion-request link entered in the Data safety form; Apple requires in-app deletion (guideline 5.1.1(v)). Audit the OSS client for an existing flow; if absent, build pre-launch. Deletion must remove associated Firestore user data; document any retention (fraud/legal) in the privacy policy. The web resource lives on the Cloudflare-zone domain (the delivery design already reserves "deletion pages").

### 5.4 Release

Android keystore + Play App Signing; iOS signing under existing account. Internal tracks first (Play internal testing + TestFlight) including a **real test purchase visible in RevenueCat**. Staged rollout (Play percentage, iOS phased release). Crashlytics dSYM upload and Android baseline profile verified. A written **launch checklist** is the ship-day contract (incident-derived additions over time), seeded with: repeatable publish builds + canary + staged rollout; written timeout/retry policy per call path; graceful-degradation verification; "products attached to entitlement"; plus the delivery-design §13 items (disaster drill rebuilding the bucket from masters + content repo, read-back validation green in CI, never-challenge rule verified on real devices, scoped tokens only, canary + takedown rehearsals on staging, AWS SDK ↔ R2 checksum mode pinned, masters snapshot job green). **Platform compliance current at launch (H3):** Android target API 36 and 16 KB page-size support; iOS privacy manifests (`PrivacyInfo.xcprivacy`) for app + SDKs; EU DSA trader status declared in both consoles.

---

## 6. Error handling summary

| Failure | Behavior |
|---|---|
| Pipeline validation fails | Run aborts before any upload |
| Upload interrupted | Live catalog untouched (clients only follow the RC pointer); re-run — immutable names make re-PUT idempotent |
| Bad catalog reaches canary | Canary telemetry catches it (abort thresholds written down, e.g. decode-failure > 0.5% of canary syncs); RC default never flips; repoint canary condition to N−1 |
| Bad catalog fully live | RC edit to N−1 (retained); propagates within the jittered fetch interval |
| `schemaVersion > maxSupported` | Client freezes on cached catalog + surfaces "update app" — never bricks |
| Client fetch fails | Backoff+jitter retries (network/5xx only), then `Failed(retryAt)`; UI serves cache |
| R2 / Cloudflare outage | App fully functional on cache (first launch: bundled seed catalog); sync resumes on recovery — blast radius is "content is a day late" |
| Consent unavailable / ads fail | App works ad-free; no retry loops on ad load failure |
| RevenueCat unreachable | Cached CustomerInfo + offline entitlement fallback |

---

## 7. Research provenance

Two research passes (adversarial verification interrupted early to cap token cost; findings tagged):
- **Engineering:** confirmed — SRE launch checklist/retry/jitter/staged rollout, Confluent compatibility semantics, kotlinx.serialization evolution mechanics, Etsy atomic pointer-flip. Unverified — offline-first specifics, background-scheduling specifics, image delivery, all AdMob/RevenueCat items (spot-check during implementation).
- **AI generation:** confirmed — FLUX pricing ballpark, FLUX.1-schnell Apache-2.0 (commercial-safe), FLUX.1-dev weights non-commercial (outputs via licensed APIs OK), SD 3.5 commercial < $1M/yr, USCO Jan-2025: prompt-only AI images have no US copyright (moat = curation/brand/freshness, not image IP), hosted models cap ~4 MP (upscale stage is structural). Unverified — Leonardo terms, GPU-rental spot rates, Play AI-content policy.
- **Delivery (R2 + Cloudflare):** web-verified 2026-07-02 in the delivery design §15 — zero egress, pricing/free tier, no object versioning yet, edge-cache behavior, purge tiers, `r2.dev` non-production status. Incident timelines and AWS-SDK checksum history are from memory (medium confidence).
- **External review:** all findings landed — C1 (licensing exposure → takedown as first-class flow), C2 (cache invalidation → content-addressed naming), C3 (canary mechanics + telemetry → delivery design §7/§12.4), C4 (account deletion → §5.3), H1 (host choice → explicit R2 decision), H2 (masters → dedicated bucket + meta-only git, §3.1), H3 (platform compliance → §5.4), H4 (Apache-2.0 compliance → §5.2). Second-pass review also resolved three internal conflicts (allowlist-vs-denylist, status-field-vs-directory, masters-in-git) and added the string-map-keys, frozen-runtime, metadata-stripping, and cross-repo-invocation rules to the schema doc.
