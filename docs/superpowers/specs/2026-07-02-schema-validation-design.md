# Schema & Validation Design — Content Contract

**Date:** 2026-07-02
**Status:** Design-rationale record — all deltas applied inline in ship plan v2. Amends ship plan §3.1, §3.2 Stage 1, §3.4.
**Companion to:** [2026-07-02-ship-plan-design.md](2026-07-02-ship-plan-design.md), [2026-07-02-delivery-r2-design.md](2026-07-02-delivery-r2-design.md)

> Source note: this document captures the schema and validation sections of the external design review. Its delivery section (Firebase Hosting for catalogs + Storage for media) was **superseded** by the R2 consolidation — see the delivery design's rejected-alternatives table. The review's findings C1 (licensing exposure) and C2 (cache invalidation) are addressed by takedown-as-first-class-flow and content-addressed naming respectively; H1 (host choice) is now an explicit decision. C3 (canary mechanics + telemetry) is captured in the delivery design §7 and §12.4; C4 (account deletion, store-approval blocker) is captured in ship plan §5.3; H2 (masters storage) is resolved in ship plan §3.1 — masters bucket + meta-only git.

---

## 1. Bucket layout (agrees with delivery design §4)

```
media/<wallpaperId>/<hash8>-<class>.webp    # immutable forever, content-hashed
catalog/<version>.json                       # immutable
```

Hash in the filename gives cache-bust-by-content; the `wallpaperId` prefix keeps takedowns a one-prefix delete — which matters given the licensing exposure. Version directories contain **JSON only**. Unchanged wallpapers across drops = identical URLs = warm caches survive the flip.

---

## 2. meta.yaml schema (structured, not freetext — this is what validation grips)

```yaml
id: wp-2026-07-0042          # must match dir name
title: ...
category: abstract            # from committed enum file
tags: [gradient, dark]
tier: free | reward-unlock | plus-only
source: ai | photo-licensed | own
license:
  type: flux-api | direct-artist | own   # closed allowlist — the primary gate
  detail: "FLUX.2 klein via BFL API"
  agreement_ref: licenses/artist-x.pdf   # required if photo-licensed
ai:                           # required if source: ai
  model: flux.2-klein
  provider: bfl
  seed: 481923
  prompt_ref: prompts/house-style-v3.md
status: staged | published | retired
retired_at: 2026-09-14        # set on retirement; gates two-phase media deletion (delivery design §9)
```

State has one source of truth: the `status` field. `candidates/` holds raw generation output with **no meta.yaml**; promotion creates `wallpapers/<id>/` (location only distinguishes raw from promoted — there is no `candidate` status).

---

## 3. Catalog contract

Top level: `schemaVersion` (int), `catalogVersion`, `generatedAt`, `wallpapers[]`, `collections[]`.

Per wallpaper: `id`, `title`, `category`, `tags`, `tier`, `publishedAt`, `renditions: {class → {url, width, height, bytes}}`, optional `avgColor`/`blurhash` (compute in Stage 2, additive field, big placeholder-UX win when the client grows support).

**Evolution invariants, CI-enforced:**

- Additive only; every new field has a default; no removals/retypes/renames.
- **Every enum-typed field has a default.** This is the trap `ignoreUnknownKeys` doesn't cover: add a fourth `tier` value and every old client throws on decode unless `coerceInputValues` has a default to coerce to. Make "enum without default in catalog models" a review blocker alongside "nullable without default" — this is precisely the bug class the frozen-decoder gate exists to catch, so make the gate's job easy.
- `schemaVersion` freeze behavior in the client from day one: if `schemaVersion > maxSupported`, keep serving the cached catalog and surface "update app." That's the escape hatch for a someday-breaking change — old clients freeze on last-good instead of bricking. Impossible to retrofit.
- **Rendition map keys are `String`, never an enum.** `coerceInputValues` rescues unknown enum *values*, not unknown enum *map keys* — a `renditions: Map<DeviceClass, Rendition>` would throw on a new device class and brick every old client. Client models use string keys; unknown classes are ignored data.

---

## 4. Validation

One rule library, two invocation points: **pre-render** (PR CI on the content repo) and **pre-publish** (full chain in the publish job). Governing principle: producer strict, consumer tolerant — unknown keys are an *error* in the pipeline and *ignored* in the client.

**Metadata:** yaml parses strictly against schema; `id` == dirname, globally unique; `category` ∈ committed enum file (enforce at the source); `license.type` ∈ its closed allowlist — the primary gate; a **denylist substring check** `{unsplash, pexels, pixabay, standard-royalty-free}` runs against freetext fields (`license.detail`, `agreement_ref` filename) as defense-in-depth, so "sourced from Unsplash" in a detail string still fails the build; `photo-licensed` requires `agreement_ref` resolving to a real file in-repo; `ai` requires model ∈ commercially-safe allowlist.

**Masters:** exists **in the masters bucket** (multi-GB masters do not live in git — H2, ship plan §3.1), decodes, dimensions ≥ largest rendition target, aspect within per-class tolerance, sRGB, no alpha, byte-size ceiling.

**Renditions (post-Stage-2):** every expected class present; each WebP **re-decodes** (corrupt-encode gate); dims exactly match class spec; bytes within [floor, ceiling] — the floor catches black/empty encodes; pHash distance vs all published wallpapers above threshold (near-dupe gate across drops); mean-luminance sanity check; **all EXIF/PNG-text metadata stripped** — masters retain generation provenance (the audit trail), renditions must never leak prompts/seeds to anyone with `exiftool`.

**Catalog:** serialize via app models → round-trip decode-equality with current models → **decode with frozen oldest-supported models**. Pin the frozen artifact concretely: serializer classes compiled from a tagged app-release commit, checked in as a versioned jar the publish job consumes — not "checkout old branch and hope." The jar also pins (shades) the exact kotlinx.serialization **runtime** version that release shipped — frozen models running on today's runtime test the wrong thing, because decode behavior differs across runtime versions. Plus a static scan of serial descriptors asserting the default-on-every-optional/enum rule, so the invariant is mechanical, not review-dependent.

**Referential:** every `url` in the candidate catalog resolves to either an object staged in this publish or a live object (HEAD against the bucket for reused media); every catalog entry has a master in the masters bucket (HEAD check); every id in a drop yaml exists under `wallpapers/`; orphaned media = warning, not error.

**Output:** machine-readable JSON report + human summary, non-zero exit on any error, warnings listed but non-blocking.

**Invocation mechanics:** the validator and the frozen-decoder jar are built in the app repo (`service/service-content-pipeline`) and published as a **version-pinned fat-jar/container** that the content-repo CI consumes — content CI never checks out the app repo.

---

## 5. Generation & listing addenda (from the review tail)

- Prefer perceptual-hash dedupe across drops (see §4 renditions rule).
- **Generate at 2–4 MP and upscale 2×**, rather than 4× from 1 MP.
- Evaluate Real-ESRGAN specifically on **OLED-dark gradients**, where banding/oversmoothing shows most.
- **iOS review risk under App Store guidelines 4.2/4.3 is real for wallpaper apps** — lean on collections, Plus, and live features in the store listing to demonstrate differentiation beyond "a grid of images."

---

## 6. Ship plan deltas

- Pointer → RC param (or pointer file fallback if RC wiring is heavy) — captured in delivery design.
- Catalogs + media → content-addressed stable namespace with absolute URLs — captured in delivery design.
- Enum-default rule + `schemaVersion` freeze added to ship plan §3.4.
- Validation spec above replaces the one-liner in ship plan §3.2 Stage 1.
- meta.yaml schema above replaces the sketch in ship plan §3.1.
