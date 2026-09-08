# Delivery Design — R2 + Cloudflare CDN Content Distribution

**Date:** 2026-07-02
**Status:** Approved (R2 green-lit). Design-rationale record — all deltas applied inline in ship plan v2. Amends ship plan §1 non-goals, §2, §3.2 Stage 4, §3.3, §5.3–5.4.
**Companion to:** [2026-07-02-ship-plan-design.md](2026-07-02-ship-plan-design.md)

---

## 1. Decision summary

| Decision | Choice |
|---|---|
| Media + catalog host | **Single R2 bucket**, custom domain, Cloudflare CDN in front |
| Version signal (pointer) | **Firebase Remote Config** param `catalogVersion` — percentage condition = canary, param edit = rollback |
| Media naming | **Content-addressed, stable across catalog versions** — unchanged wallpapers keep identical URLs across drops |
| Firebase Hosting | **Out of the content path** (legal/deletion pages only, or Cloudflare Pages) |
| Firebase Storage | **Not used for content** |
| Firebase keeps | Auth, Firestore, Remote Config, FCM, Crashlytics, Analytics |

Architectural doctrine preserved: no live API server. Bucket + CDN + managed RC — nothing we operate. "Virality is a billing event" is eliminated: egress is structurally $0 at any install count.

### Rejected alternatives (decision history)

| Option | Why rejected |
|---|---|
| Firebase Storage direct | No CDN in front of `firebasestorage.googleapis.com`; egress ~$0.12/GB scales 1:1 with installs |
| Firebase Hosting for catalogs + Storage for media | Hosting rewrites cannot proxy Storage (rewrites target local files / Functions / Cloud Run only — the Medium "rewrite to Storage" pattern does not work); a Function proxy reintroduces a running service. Superseded by R2 consolidation: one origin, one upload target, one token |
| S3 + CloudFront | Wins only on Object Lock / Lambda triggers / query-in-place / cross-region replication compliance — none apply. CloudFront egress ~$0.085/GB is the whole bill at scale |
| GCS + Cloud CDN | Keeps single-cloud with Firebase, but external LB fixed baseline (~$18/mo) exceeds entire projected R2 bill |
| Backblaze B2 + Cloudflare | Saves ~$0.009/GB-mo storage = cents at our volume, for a second vendor |
| Bunny.net | Legit media-CDN rival; nonzero egress, no S3 API parity, no operator familiarity |
| Redis / any cache service | No request path exists to cache. Client committed catalog + HTTP caching + CDN edge are the three cache layers, none operated by us |

---

## 2. Verified facts (web-verified 2026-07-02)

- **Zero egress** for all storage classes via S3 API, Workers API, and public domains — confirmed current in Cloudflare docs (June 2026). No caps, no fair-use ratio, no throttling.
- **Pricing:** $0.015/GB-mo standard storage; Class A $4.50/M, Class B $0.36/M. **Free tier: 10 GB storage, 1M Class A, 10M Class B per month.**
- **CDN cache hits do not touch R2** — enabling Cloudflare Cache on the custom domain lets reads bypass the R2 Gateway entirely (served from edge). Perf + resilience + near-zero Class B billing.
- **No object versioning** — still roadmap-only per R2 team (Dec 2025), not shipped as of this check. Delete-protection gap; mitigated in §11.
- **Purge:** per-URL purge free on all plans; purge-by-prefix/tag is Enterprise-only. Immutable naming means we never purge for updates — only takedowns (bounded URL count per wallpaper).
- **`r2.dev` subdomain is rate-limited, not for production.** Custom domain is mandatory → zone must be on Cloudflare.
- **Incident record (from memory, shapes the risk model):** Feb 2025 R2 Gateway incident (~1h global R2 read/write failure, operator error); Nov 18 2025 global Cloudflare outage (hours of widespread 5xx, oversized config file). Absorbed by client resilience contract — see §11.

---

## 3. Topology

```
App boot / refresh (jittered per ship plan §4.3)
  │
  ├─ Remote Config fetch ──► catalogVersion = "20260702-01"        (Firebase, managed)
  │
  ├─ GET https://media.<domain>/catalog/20260702-01.json           (R2 via CF edge, immutable)
  │     └─ decode → validate → commit (sync state machine, §4 ship plan)
  │
  └─ GET https://media.<domain>/media/<id>/<hash8>-<class>.webp    (R2 via CF edge, immutable)
```

Steady state: RC fetch (SDK-managed, jittered) + long-lived immutable objects from edge cache. Cold path hits R2 origin once per object per region (tiered cache).

---

## 4. Bucket layout & naming

```
r2://<app>-content-prod          # location hint: APAC (near pipeline + user base)
  media/<wallpaperId>/<hash8>-<class>.webp    # immutable forever
  catalog/<version>.json                       # immutable; version = YYYYMMDD-NN
r2://<app>-content-staging       # same layout; RemoteApiExportSpecStaging target
```

Rules:

- `<hash8>` = first 8 hex of sha256 of rendition bytes. Content change ⇒ new URL ⇒ cache-bust by construction. Byte-identical rendition across drops ⇒ identical URL ⇒ **warm client caches survive every catalog flip** (fixes the C2 cache-invalidation flaw).
- `<wallpaperId>` prefix keeps takedown a bounded operation: one prefix, ~6 objects, ~6 per-URL purges.
- `catalog/` contains **JSON only**. No media under version paths, ever.
- `YYYYMMDD-NN` sequence suffix prevents same-day republish collision.
- **Catalog carries absolute `url` strings per rendition** (or a single `mediaBaseUrl`). This is the exit hedge: host migration = `rclone sync` + one catalog publish, zero client change. MANDATORY — do not let the client construct URLs from ids.
- Separate bucket from Daily Scenes R2: blast radius + billing clarity. Separate staging/prod buckets with separate tokens.

---

## 5. Cloudflare zone configuration

| Item | Setting |
|---|---|
| DNS | `media.<domain>` proxied (orange cloud); zone on Cloudflare (needed for §5.3 domain anyway) |
| R2 custom domain | Attach `media.<domain>` to bucket; enable **Cloudflare Cache** on it |
| Tiered Cache | **Smart Tiered Cache: ON** — one cold origin fetch per region per drop |
| Cache rules | Respect origin `Cache-Control` (default behavior; do not override with zone-level TTLs) |
| Cache Reserve | Skip — catalog is KBs, media set is GBs and hot; not worth the fee at this size |
| **Bot protection carve-out** | **MANDATORY:** WAF/security rule to never challenge `media.<domain>`. Mobile HTTP clients can be hit by Bot Fight Mode / managed challenges → catalog fetch fails in the field with no user-visible cause. Hostname serves public immutable content; challenges add zero value. Verify from real devices on non-Cloudflare networks in E2E staging |
| TLS / HTTP | Defaults fine; HTTP/3 on |
| Upload perf | Enable Local Uploads on the bucket if pipeline→APAC upload latency bites (optional) |

---

## 6. Object metadata (set at PUT, via S3 API)

| Object | Headers |
|---|---|
| `media/**` | `Cache-Control: public, max-age=31536000, immutable` · `Content-Type: image/webp` |
| `catalog/*.json` | `Cache-Control: public, max-age=31536000, immutable` · `Content-Type: application/json` |

No mutable pointer object exists in v1 — Remote Config is the pointer. ETags are automatic; conditional GETs work but immutable + hashed names make revalidation moot.

**Fallback design** (only if RC wiring into the OSS client proves heavy — see §12 audit): `pointer/current.json` on the same domain with `Cache-Control: no-cache`; steady-state refresh is a ~300-byte 304. Cost of fallback: no percentage canary (partially covered by Play staged rollout + TestFlight phased release).

---

## 7. Version signal — Remote Config

- Param: `catalogVersion` (string, `"20260702-01"`).
- **Canary:** RC percentage condition (stable per-instance hashing is built in — no hand-rolled install hashing) serves new version to 5%. Bake ≥24h against §12 telemetry. Abort = repoint the condition to N−1; no purge, no upload, nothing else.
- **Promote:** default condition → new version.
- **Rollback post-promotion:** edit param to N−1. Propagation bounded by client `minimumFetchInterval` (12h, jittered — matches ship plan §4.3). Real-time RC listener is a later optimization, not v1.
- While in RC, add two kill switches (cheap insurance): `minSupportedAppVersion` (freeze-on-cache + "update app" path, pairs with `schemaVersion` freeze behavior) and `adsEnabled`.

---

## 8. Publish pipeline — Stage 4 rewrite

Strict order, CI-only (no laptop publishes):

1. **PUT media** — all new renditions via S3 API (AWS Kotlin SDK against R2 endpoint; slots into / replaces `RemoteApiStorageManager`). Headers per §6 at PUT time. Idempotent: immutable names; HEAD-before-PUT or blind re-PUT of identical bytes both safe.
2. **PUT catalog** — `catalog/<version>.json`.
3. **Read-back validation through the custom domain** (not the S3 endpoint): assert HTTP 200, sha256 of fetched bytes == local, `cf-cache-status` header present, and catalog decodes with the frozen oldest-supported serializer models. The ship plan's "fetch back through the CDN" step is now literally true.
4. **Flip RC canary condition** to `<version>` @ 5%.
5. **Bake ≥24h** against decode-failure / sync-result / crash metrics (§12.4). Abort = repoint canary to N−1.
6. **Promote** default condition to `<version>`.

Credentials: **bucket-scoped API token (Object Read & Write)** per environment, in CI secrets only. Never account-level keys. Never in the content repo.

**Impl-time verify (flagged, not resolved here):** recent AWS SDK versions enable default integrity checksums (`x-amz-checksum-crc32`) that historically broke R2 uploads before R2 added support. Pin the SDK version against a working R2 upload in CI, or set request checksum calculation to when-required. Test in staging before first prod publish.

---

## 9. Rollback, takedown, retention

- **Rollback** = RC edit to N−1. Retain N−2 and older while any RC value or installed client may reference them (unchanged from ship plan §3.3).
- **Takedown (two-phase, unchanged in principle):** drop wallpaper from next catalog → after old catalogs age out of all clients → `DELETE media/<id>/*` (one prefix) → per-URL purge of its rendition URLs. Keep rendition-class count bounded so per-URL purging stays trivial (prefix purge is Enterprise-only). "Aged out" is computable: media objects are deletable only when the id is absent from **every retained catalog version** and `meta.yaml retired_at` exceeds the client cache window (schema doc §2).
- **Never purge for content updates** — immutable hashed names make it structurally unnecessary.
- Licensing exposure (ship-plan C1) makes fast takedown a first-class flow: rehearse it once on staging.

---

## 10. Cost model

| Line | Launch (~300 wp) | Year 1 (~1.3k wp) |
|---|---|---|
| Storage (~6 renditions/wp, ~1.3 MB avg) | ~2.5 GB → **free tier** | ~10 GB → ~**$0.08/mo** over free tier |
| Class A (weekly publish, ~150 PUTs) | free tier | free tier |
| Class B (origin reads) | Mostly absorbed by CDN cache hits (cache hits don't bill R2 ops) | same |
| **Egress** | **$0 at any MAU** | **$0 at any MAU** |

Projected bill ≈ $0 for the foreseeable future. The delivery cost curve is now decoupled from install count entirely; the only scaling cost left in the system is generation (~$1–4/mo steady state per ship plan).

---

## 11. Risk ledger

| # | Risk | Exposure | Mitigation |
|---|---|---|---|
| 1 | Cloudflare/R2 outage (Feb 2025 R2 ~1h; Nov 18 2025 global, hours) | Weekly drop delayed; new-install first sync fails during window | Already priced in by ship plan §4: browsing/search/set-wallpaper run from cache; first launch runs from bundled seed catalog. Blast radius = "content is a day late" |
| 2 | No object versioning; R&W token can delete everything it can write | Bucket loss via bad script or leaked token | Content bucket is a **derived artifact**: masters bucket + git metadata + deterministic pipeline regenerate it. Make it real: **disaster drill on launch checklist** (§13). Monthly `rclone sync` snapshot of the **masters bucket** to secondary (B2 / GCS coldline) is **required** (masters are the non-derivable input) — pennies |
| 3 | Single-vendor coupling: DNS + CDN + storage all Cloudflare | Correlated failure; lock-in gravity | Accepted, named. Hedged by #4 |
| 4 | Repricing / ToS change | Cost structure shift | Exit cost deliberately tiny: S3-compatible API (`rclone sync` out) + absolute URLs in catalog (cutover = one catalog publish). Hours, not a migration project |
| 5 | Plus-only renditions publicly fetchable at discoverable URLs | Tier gating is client-side only | Accepted v1 (was equally true on Firebase Storage public-read; realistic impact ~screenshots). Upgrade path if it ever matters: tiny Worker signing URLs. Do not build for v1 |
| 6 | Cloudflare bot protections challenge the app's HTTP client | Catalog/media fetches fail in field, invisible cause | §5 never-challenge rule + real-device E2E verification (both platforms, non-Cloudflare network) |
| 7 | Same-day republish collision | Immutable object overwrite ambiguity | `YYYYMMDD-NN` sequence in version string |
| 8 | Old ToS §2.8 fear (disproportionate media on CDN) | None | Removed/rewritten 2023; R2-with-custom-domain media serving is the intended product use |

---

## 12. Client audit items (gate implementation start)

1. **URL consumption:** does the OSS client load media from plain HTTPS URLs in the catalog, or `gs://` paths via the Firebase Storage SDK? If SDK paths → refactor to URL-driven loading **now**, before an install base exists. This single audit decides how much of §4/§6 is config vs code.
2. **Remote Config wiring:** present in client? If yes → read `catalogVersion` at the `CheckingPointer` state. If threading RC into the sync machine is a big diff → pointer-file fallback (§6) and accept no percentage canary for v1.
3. **Media loader** (Coil / platform loader) honors HTTP cache headers; media via platform download managers per ship plan §4.2.
4. **Canary telemetry exists before first canary:** Crashlytics non-fatal + analytics events `catalog_decode_failed{version}`, `catalog_sync_result{version, outcome}`. Without these the 24h bake is theater. Write abort thresholds down (e.g., decode-failure rate > 0.5% of canary syncs, or any crash spike attributable to sync → abort).

---

## 13. Launch checklist additions

- [ ] Disaster drill: rebuild prod bucket contents from masters bucket + content repo into staging; app passes E2E against it
- [ ] Read-back validation through custom domain with byte-hash + `cf-cache-status` assertions, green in publish CI
- [ ] Never-challenge rule verified from real devices, both platforms, non-Cloudflare network
- [ ] Bucket-scoped tokens per env in CI secrets; zero account-level keys anywhere; content repo contains no credentials
- [ ] Canary rehearsal on staging: flip RC condition to 5% → confirm only hashed cohort updates → abort to N−1 → confirm recovery
- [ ] Takedown rehearsal: catalog drop → prefix delete → per-URL purge, on staging
- [ ] Masters-bucket snapshot job green (required — see risk ledger #2)
- [ ] AWS SDK ↔ R2 upload compatibility pinned (checksum mode) in staging CI

---

## 14. Ship plan deltas (line-by-line)

- **§1 non-goals:** "dedicated image CDN" moves from non-goal to **shipped in v1** (R2 + Cloudflare). Add non-goal: server-side search.
- **§2 Foundation:** add — Cloudflare zone setup, `media.<domain>`, prod/staging buckets, scoped tokens, never-challenge rule.
- **§3.2 Stage 4:** replaced by §8 above (S3 PUT path, headers at PUT, domain read-back, RC canary/promote).
- **§3.3 Storage/caching/rollback:** replaced by §4, §6, §7, §9 above (R2 layout, immutable naming, RC pointer, purge policy). Pointer-file mechanics deleted unless §12.2 fallback triggers.
- **§5.3:** domain's DNS lives on Cloudflare zone (also hosts privacy/ToS/deletion pages via Pages or elsewhere).
- **§5.4:** checklist seeded with §13 items.

---

## 15. Provenance

- **Web-verified 2026-07-02:** zero egress (Cloudflare R2 docs, current June 2026); pricing + free tier; versioning not shipped (R2 team, Dec 2025, "roadmap"); edge cache bypassing R2 Gateway on custom domains; per-URL vs Enterprise prefix purge; `r2.dev` non-production status.
- **From memory (medium confidence, verify only if load-bearing):** exact incident timelines/durations (Feb 2025, Nov 18 2025); ToS §2.8 removal date; AWS SDK checksum-header R2 breakage history.
- **Deferred to implementation:** AWS Kotlin SDK ↔ R2 quirks (§8 flag); Local Uploads benefit; whether OSS client needs the pointer-file fallback (§12.2).
