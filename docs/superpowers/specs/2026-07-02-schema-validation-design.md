# Schema & Validation Design — Content Contract

**Date:** 2026-07-02
**Status:** Approved alongside the R2 delivery design. Amends ship plan §3.1, §3.2 Stage 1, §3.4.
**Companion to:** [2026-07-02-ship-plan-design.md](2026-07-02-ship-plan-design.md), [2026-07-02-delivery-r2-design.md](2026-07-02-delivery-r2-design.md)

> Source note: this document captures the schema and validation sections of the external design review. Its delivery section (Firebase Hosting for catalogs + Storage for media) was **superseded** by the R2 consolidation — see the delivery design's rejected-alternatives table. The review's findings C1 (licensing exposure) and C2 (cache invalidation) are addressed by takedown-as-first-class-flow and content-addressed naming respectively; H1 (host choice) is now an explicit decision. Findings C3 and C4 were lost to truncation in transfer — re-capture before implementation planning.

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
  type: flux-api | direct-artist | own
  detail: "FLUX.2 klein via BFL API"
  agreement_ref: licenses/artist-x.pdf   # required if photo-licensed
ai:                           # required if source: ai
  model: flux.2-klein
  provider: bfl
  seed: 481923
  prompt_ref: prompts/house-style-v3.md
status: candidate | published | retired
```

---

## 3. Catalog contract

Top level: `schemaVersion` (int), `catalogVersion`, `generatedAt`, `wallpapers[]`, `collections[]`.

Per wallpaper: `id`, `title`, `category`, `tags`, `tier`, `publishedAt`, `renditions: {class → {url, width, height, bytes}}`, optional `avgColor`/`blurhash` (compute in Stage 2, additive field, big placeholder-UX win when the client grows support).

**Evolution invariants, CI-enforced:**

- Additive only; every new field has a default; no removals/retypes/renames.
- **Every enum-typed field has a default.** This is the trap `ignoreUnknownKeys` doesn't cover: add a fourth `tier` value and every old client throws on decode unless `coerceInputValues` has a default to coerce to. Make "enum without default in catalog models" a review blocker alongside "nullable without default" — this is precisely the bug class the frozen-decoder gate exists to catch, so make the gate's job easy.
- `schemaVersion` freeze behavior in the client from day one: if `schemaVersion > maxSupported`, keep serving the cached catalog and surface "update app." That's the escape hatch for a someday-breaking change — old clients freeze on last-good instead of bricking. Impossible to retrofit.

---

## 4. Validation

One rule library, two invocation points: **pre-render** (PR CI on the content repo) and **pre-publish** (full chain in the publish job). Governing principle: producer strict, consumer tolerant — unknown keys are an *error* in the pipeline and *ignored* in the client.

**Metadata:** yaml parses strictly against schema; `id` == dirname, globally unique; `category` ∈ committed enum file (enforce at the source); `license.type` present and **∉ denylist** `{unsplash, pexels, pixabay, standard-royalty-free}`; `photo-licensed` requires `agreement_ref` resolving to a real file in-repo; `ai` requires model ∈ commercially-safe allowlist.

**Masters:** exists, decodes, dimensions ≥ largest rendition target, aspect within per-class tolerance, sRGB, no alpha, byte-size ceiling.

**Renditions (post-Stage-2):** every expected class present; each WebP **re-decodes** (corrupt-encode gate); dims exactly match class spec; bytes within [floor, ceiling] — the floor catches black/empty encodes; pHash distance vs all published wallpapers above threshold (near-dupe gate across drops); mean-luminance sanity check.

**Catalog:** serialize via app models → round-trip decode-equality with current models → **decode with frozen oldest-supported models**. Pin the frozen artifact concretely: serializer classes compiled from a tagged app-release commit, checked in as a versioned jar the publish job consumes — not "checkout old branch and hope." Plus a static scan of serial descriptors asserting the default-on-every-optional/enum rule, so the invariant is mechanical, not review-dependent.

**Referential:** every `url` in the candidate catalog resolves to either an object staged in this publish or a live object (HEAD against the bucket for reused media); every catalog entry has a master in git; every id in a drop yaml exists under `wallpapers/`; orphaned media = warning, not error.

**Output:** machine-readable JSON report + human summary, non-zero exit on any error, warnings listed but non-blocking.

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
