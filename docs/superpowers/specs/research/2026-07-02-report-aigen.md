# AI Wallpaper Generation for a Commercial Wallpaper App — Findings Report

**Scope:** Solo developer; ~100–300 images for launch, then ~10–50/week; portrait ~1440x3200 (≈4.6 MP) up to 4K plus tablet; commercial use of outputs mandatory. Decision: hosted API vs self-hosted open model vs local Apple Silicon.

**Verification key:** [CONFIRMED] = an adversarial verification vote checked the claim against a primary source and confirmed it. [UNVERIFIED] = no vote covered it. [REFUTED — excluded] claims are listed at the end and are NOT used in recommendations. Note: the digest's per-claim `src:` fields were empty; where a verification vote identified the primary source URL, that URL is cited. For unverified claims, the source is named as described in the digest (URL not preserved).

---

## 1. Hosted API pricing landscape (per-image costs)

### Black Forest Labs (FLUX) — official API (bfl.ai)
- **FLUX.1 legacy endpoints are flat-priced per image regardless of resolution:** FLUX.1 [dev] $0.025, FLUX 1.1 [pro] $0.04, FLUX.1 [pro] $0.05, FLUX 1.1 [pro] Ultra $0.06, FLUX.1 Kontext [pro] $0.04, FLUX.1 Kontext [max] $0.08. [CONFIRMED — three votes parsed the live JSON-LD schema at https://bfl.ai/pricing, 2026-07-02]
- **FLUX.2 is priced per megapixel:** FLUX.2 [pro] $0.03 first MP + $0.015/additional MP (≈$0.08–0.09 for a native 4.6 MP 1440x3200 wallpaper); FLUX.2 [max] $0.07 + $0.03/MP; FLUX.2 [flex] flat $0.05/MP. [CONFIRMED — https://bfl.ai/pricing; a vote also checked the 1440x3200 = 4.608 MP arithmetic]
- **Cheapest official FLUX endpoints — FLUX.2 [klein]:** klein 4B = $0.014 first MP + $0.001/additional MP; klein 9B = $0.015 + $0.002/MP. A ~4.6 MP phone-wallpaper-resolution image costs **≈$0.018–0.022**. [CONFIRMED — three votes, https://bfl.ai/pricing]
- BFL API has no subscription/seat fees — pure pay-per-generation, which suits 10–50 images/week. [UNVERIFIED — bfl.ai/pricing]

### OpenRouter (FLUX.2 Pro)
- Output billed $0.03 first MP + $0.015/additional MP → a max-size 4 MP image ≈ **$0.075**. [CONFIRMED — https://openrouter.ai/black-forest-labs/flux.2-pro, live 2026-07-02]
- **FLUX.2 Pro supports text-to-image/editing only up to 4 MP** — below the ~4.6 MP needed for native 1440x3200. Native full-res phone wallpapers are not possible on this endpoint; an upscale step is required. [CONFIRMED — three votes; corroborated by BFL's own docs (bfl.ai/blog/flux-2, docs.bfl.ai)]
- Input reference images charged $0.015/MP. [CONFIRMED — same OpenRouter pricing page]
- FLUX.2 Pro released 2025-11-25 [release date corroborated in passing by a vote]; served by a single provider (BFL) with no multi-provider routing [UNVERIFIED].

### Aggregators & normalized comparisons
- Price Per Token normalized comparison (July 2026, Replicate + fal.ai): cheapest hosted price **$0.0002 per 1024x1024** (sdxl-turbo). [UNVERIFIED — Price Per Token; URL not preserved]
- FLUX.1 Schnell ≈ **$0.0005**/1024x1024 hosted — ~18x cheaper than FLUX.1 Dev ($0.0090) and ~80x cheaper than FLUX 1.1 Pro ($0.040) on the same basis. [UNVERIFIED — Price Per Token]
- Other normalized mid-2026 prices: SDXL Base $0.0019, SD 3.5 Medium $0.030, SD 3.5 Large $0.065, Qwen Image $0.021, HiDream I1 Full $0.0090, Ideogram 3.0 $0.060, Imagen 4 Fast $0.020, Recraft V3 $0.040. [UNVERIFIED]
- Billing bases differ — Replicate per GPU-second, fal.ai per image/MP, Together per MP or flat, OpenAI per output token — and costs rise above the 1024x1024 baseline, which matters at wallpaper resolutions. [UNVERIFIED]
- SD 3.5 on fal.ai/Together ≈ $0.008–0.012/image; Replicate $0.012–0.015 (April 2026). [UNVERIFIED]
- Price spread across hosted APIs is roughly 25x cheapest-to-most-expensive tier (~$120–400 vs $4K–18K per million images). [UNVERIFIED]

### Other providers
- **OpenAI (June 2026):** GPT Image 2 $0.05/img, GPT Image 1.5 $0.04 (rated quality leader), GPT Image 1 Mini $0.005. [UNVERIFIED] gpt-image-1 ≈ $0.04–0.08 at high quality; a 100–300 image catalog ≈ $4–24 total. [UNVERIFIED] Caveat: native portrait max is 1024x1536 — well below 1440x3200, so upscaling is mandatory. [UNVERIFIED]
- **Stability AI API (June 2026):** SD 3.5 Large $0.065/img; Stable Image Core $0.03/img. [UNVERIFIED]
- **Ideogram 3.0:** $0.03/img; best-in-class readable in-image text (less relevant for wallpapers). [UNVERIFIED]
- **Google Imagen 4:** Fast $0.02 / Standard $0.04 / Ultra $0.06; OpenAI/Google Batch APIs cut costs ~50% on large runs. [UNVERIFIED]
- **Midjourney:** $10/$30/$60/$120 per month tiers; Standard+ include unlimited Relax-mode generation (marginal cost →0 at low volume) [UNVERIFIED]; Fast GPU hours 3.3/15/30/60 per month, extra at $4/GPU-hr [UNVERIFIED]. **No sanctioned API / automated access prohibited** [UNVERIFIED — Midjourney ToS], and the digest contains contradictory claims about a "limited-release API" (see Conflicts note in §5). Stealth mode (private generations) only on Pro/Mega. [UNVERIFIED]
- **Leonardo:** no claims about Leonardo pricing/terms survived into the digest — treat as a research gap.

## 2. Self-hosting economics

### Rented cloud GPUs
- RTX 4090 rental prices observed: SaladCloud from $0.18/hr [UNVERIFIED], SynpixCloud ~$0.39/hr (Feb 2026) [UNVERIFIED], JarvisLabs $0.59/hr [UNVERIFIED], Spheron $0.67/hr (May 2026; H100 SXM5 spot $0.80/hr) [UNVERIFIED]. (RunPod/Vast.ai specifically were not captured in the digest — the marketplace range above is the proxy.)
- **At-utilization $/image (all at 1024x1024, NOT wallpaper resolution):**
  - FLUX.1-schnell FP8 on SaladCloud RTX 4090: up to 5,243 images/$ ≈ **$0.00019/img** (1-hr load test, 4 steps). [UNVERIFIED — SaladCloud benchmark blog]
  - FLUX.1-dev FP8 on SaladCloud: ≈ **$0.00101/img** (992 img/$, 20 steps); 99.78% success rate, <18s avg response; retries needed due to node reallocations. [UNVERIFIED]
  - JarvisLabs: FLUX.1 Dev ≈ $0.0033/img (10K images ≈ $33 / ~56 GPU-hrs); Schnell ≈ $0.0005/img. [UNVERIFIED]
  - Spheron: SDXL ~28 img/min ≈ $0.0004/img; FLUX.1 Dev FP8 ~13 img/min ≈ $0.0009/img; H100 spot SDXL ≈ $0.0002/img — but no break-even analysis, and figures ignore idle/minimum-rental time, which dominates at 10–50 images/week. [UNVERIFIED]
- **Critical caveat flagged in the digest itself:** all these benchmarks are 1024x1024, so they do not directly price native 1440x3200 generation; wallpaper output needs either higher-res (slower/costlier) generation or an upscale step. [UNVERIFIED]
- Throughput reference: RTX 4090 does Schnell in ~2–4s and Dev (20 steps) in ~15–30s at 1024x1024; H100 ($2.69/hr) ~1–2s / ~5–10s. [UNVERIFIED]
- VRAM: FLUX.1 Dev BF16 needs ~30–33GB (does not fit a 24GB 4090); FP8 (~18–23GB) fits; FLUX.2-dev on a 4090 only via GGUF Q4 (~19GB). FP8 ~8–10GB, NF4 ~6–8GB enable cheaper cards. [UNVERIFIED]

### Buying hardware
- RTX 4090 workstation: $2,500–$3,200 upfront (GPU $1,800–2,000) [UNVERIFIED]; electricity ~$23/mo at 8h/day up to ~$69/mo at 24/7 + cooling [UNVERIFIED].
- At ~60 GPU-hrs/month, buying breaks even vs cloud rental only after ~31 months; decision framework: buy only if >6 hrs/day sustained for 18+ months. [UNVERIFIED]
- **At this project's volume (≈1–3 GPU-hrs/week even with heavy curation), buying a GPU can never pay back against either rentals or cheap APIs.**

### Apple Silicon local
- FLUX 1024x1024 (~30 steps, FP16): ~85s on M4 Max, ~105s M3 Max, ~145s M2 Max, ~180s M1 Max — a 100–300 image catalog is feasible but takes hours. [UNVERIFIED — Apple Silicon FLUX benchmark article]
- RTX 4090 does the same image in ~12–18s → Apple Silicon ≈ 3–5x slower. [UNVERIFIED]
- Memory: full FLUX.1-dev needs 24–26GB loaded; 32GB+ unified memory recommended; GGUF Q4 (6–8GB) / Q6 (10–12GB) make 16–24GB Macs workable. [UNVERIFIED]
- Hardware value: RTX 4090 $1,599 vs Mac Studio M2 Ultra 64GB $4,999 — don't buy a Mac *for* generation; but a Mac the dev already owns has $0 marginal cost. [UNVERIFIED]

### Break-even vs APIs at our volume
Assume aggressive curation: 5 generations per shipped wallpaper → launch ≈ 1,500 generations; steady state ≈ 50–250 generations/week.
- Hosted klein 4B at ~2.3 MP then 2x upscale: **launch ≈ $23; steady state ≈ $0.75–$3.80/week**.
- Hosted FLUX.2 [pro] native-ish (4 MP + upscale): launch ≈ $110; steady ≈ $3.75–$19/week.
- Rented 4090 (~$0.40–0.67/hr): the whole launch batch is a single ~$3–10 rental session; weekly batches ≈ $0.50–1.50/week including setup overhead — cheaper than mid-tier APIs but with ops burden (ComfyUI setup, retries, storage) and roughly break-even with klein at these volumes.
- Bought GPU: $2,500+ upfront vs <$200/year of API spend → **never breaks even here**.
- Local Mac: $0 marginal, ~1.5–3 min/image; 250 generations/week ≈ 6–12 hrs of unattended compute — viable as overnight batch, not for interactive iteration.

## 3. Licensing (make-or-break)

**Model weights vs outputs are licensed separately. Precision matters:**

- **FLUX.1-schnell weights: Apache License 2.0** (verbatim, January 2004 text). [CONFIRMED — three votes against https://github.com/black-forest-labs/flux/blob/main/model_licenses/LICENSE-FLUX1-schnell; corroborated by the HuggingFace model card]
- **FLUX.1-schnell grant is perpetual, royalty-free, irrevocable (reproduce/modify/sublicense/distribute) → free commercial self-hosting and commercial wallpaper generation, no payment to BFL.** [CONFIRMED — three votes] This is the only fully-clean open-weights commercial path confirmed in this research.
- **FLUX.1-dev weights: "FLUX.1 [dev] Non-Commercial License v1.1.1."** Section 1(c) limits permitted use to purposes with "no direct or indirect payment arising from use of the model." Self-hosting FLUX.1-dev to power a commercial wallpaper app is **not permitted** under the default license; commercial deployment requires a separate license granted at BFL's sole discretion [that last point UNVERIFIED]. [CONFIRMED — three votes against https://github.com/black-forest-labs/flux/blob/main/model_licenses/LICENSE-FLUX1-dev]
- **But FLUX.1-dev OUTPUTS are commercially usable:** the dev license states verbatim "We claim no ownership rights in and to the Outputs" and "You may use Output for any purpose (including for commercial purposes), except as expressly prohibited herein". The restriction is on running the model commercially, not on the images. [CONFIRMED — three votes; note two votes flagged the digest's citation pointed at the schnell file when the quote lives in the dev file — verdict unchanged] Practical implication (analysis, not a verified claim): using FLUX.1-dev via a hosted API whose operator holds a BFL commercial license (BFL's own API, fal.ai, Replicate) yields commercially usable outputs without you needing a weights license.
- Dev license also: prohibits using outputs to train/distill competing models [UNVERIFIED]; requires content filtering/review of outputs and AI-generation disclosure where legally required [UNVERIFIED].
- **Stability AI Community License (SD 3.5 suite, SDXL Turbo, etc.): free commercial use below USD $1M annual revenue** (any source). [CONFIRMED — three votes against https://stability.ai/license and https://stability.ai/community-license-agreement (effective 2024-07-05)]
- **You own SD3.5 outputs** (from Core Models and Derivative Works incl. LoRA fine-tunes) and may use them at your discretion — generated wallpapers can be sold. [CONFIRMED — two votes, same primary sources]
- Over $1M revenue → paid Stability Enterprise License required [UNVERIFIED, but consistent with the confirmed "free unless >$1M" text]. Community License is revocable on AUP violation [UNVERIFIED]. Base SDXL 1.0 was NOT confirmed to be under the Community License by the checked page [UNVERIFIED] — verify separately before relying on SDXL base.
- **Midjourney:** paying users own their assets, but companies >$1M gross annual revenue must be on Pro/Mega for commercial rights [UNVERIFIED]; users grant Midjourney a perpetual, irrevocable, sublicensable license over prompts AND outputs — **wallpapers cannot be kept exclusive** [UNVERIFIED]; images public by default below Pro/Mega [UNVERIFIED]; free users get only CC BY-NC [UNVERIFIED]; automated access prohibited [UNVERIFIED].

### Copyright & store policy
- **US Copyright Office (Part 2 report, Jan 2025): purely AI-generated images are not copyrightable** — "Copyright does not extend to purely AI-generated material, or material where there is insufficient human control over the expressive elements." [CONFIRMED — two votes extracted the quote from the copyright.gov PDF]
- **Prompts alone — however detailed — do not confer authorship** under current technology. [CONFIRMED — vote verified verbatim] Iterative re-rolling/curation likewise doesn't create per-image copyright [UNVERIFIED]; creative modification, human-authored img2img inputs, or the curated catalog as a compilation can be protectable [UNVERIFIED].
- The Office recommended no new legislation — non-copyrightability of prompt-only outputs is the settled US baseline. [CONFIRMED — vote 38 verified the report's conclusions] **Business consequence: your catalog images themselves cannot be copyright-enforced against copycats; moats must come from brand, curation, and app experience.**
- **Google Play:** the AI-Generated Content policy covers apps with in-app AI generation [UNVERIFIED]; apps that only distribute a pre-generated curated catalog are currently **exempt** from that policy [UNVERIFIED]; developers remain responsible for offensive/deceptive content, and sexually-gratifying AI apps / deepfake sexual material are named violations [UNVERIFIED — Google Play policy pages].

## 4. Quality & consistency

- **Native high-res generation is the wrong approach with FLUX.1-dev:** white-halo artifacts above ~2 MP (1440x3200 is ~4.6 MP) [UNVERIFIED]; native 4K rated "Poor" with ~4-min renders [UNVERIFIED]; best at 1920x1080 ("Excellent") [UNVERIFIED]; render time scales steeply (20s @1024² → 240s @4K) [UNVERIFIED]. Recommended: iterate at 512², finalize at moderate res, then upscale. [UNVERIFIED]
- The 4 MP hard cap on FLUX.2 Pro endpoints independently forces the same generate-then-upscale pattern. [CONFIRMED — §1]
- **Upscaling:** Real-ESRGAN is free, local, ~3s per 1080p 4x upscale, runs in as little as 2GB VRAM — the only practical batch choice [UNVERIFIED]; SUPIR highest quality but 10–50x slower, 12–16GB VRAM — good for a small curated catalog [UNVERIFIED]; Topaz Photo AI $199/yr or $299 perpetual, local, no per-image fees [UNVERIFIED].
- **Style consistency:** FLUX LoRA training needs only ~25–30 images [UNVERIFIED], ~40 steps/image (~1,040 steps) [UNVERIFIED], 1024² training images suffice even for wallpaper-res outputs [UNVERIFIED], LR ~0.0004 [UNVERIFIED]; LoRA is the core technique for catalog-wide style uniformity [UNVERIFIED]. Note licensing interplay: BFL's paid tiers advertise LoRA rights for FLUX.2 models, and schnell (Apache 2.0) can be LoRA-tuned freely; SD3.5 fine-tunes are Derivative Works whose outputs you own [CONFIRMED — Stability license]. Cheaper alternative: prompt-template style modifiers, which gpt-image-1 applies more uniformly than DALL-E 3 [UNVERIFIED], with ~2% vs ~15% constraint-failure rate on no-text/no-watermark/portrait constraints [UNVERIFIED] — constraint-failure rate directly drives curation cost per usable wallpaper.
- FLUX.2 Pro markets consistent character/style across multi-reference inputs (references billed $0.015/MP [CONFIRMED]). [UNVERIFIED as to the consistency claim]

## 5. Bottom line — cheapest sane pipeline

**Primary recommendation: hosted API generation + free local upscale.**
1. Generate portrait candidates at ~2–2.3 MP (e.g., 1080x2160) via **BFL FLUX.2 [klein] 4B API**: ≈ **$0.015–0.016/image** at that size ([CONFIRMED pricing]; klein weights are also reported free/Apache-licensed on HuggingFace per the refutation evidence for claim 29, but self-host licensing of klein remains [UNVERIFIED] — API use sidesteps it). Alternative at even lower cost: **FLUX.1-schnell via fal.ai/Replicate at ~$0.0005–0.003/image** [pricing UNVERIFIED; license CONFIRMED Apache 2.0].
2. Curate at 5:1, then **2x upscale winners locally with Real-ESRGAN** on the dev's Mac (free) to 2160x4320-class, downsample to exact 1440x3200 / 4K / tablet crops.
3. Style consistency via a fixed prompt-template style block first; add a schnell/klein LoRA (~25–30 images, one cheap training run) only if the catalog drifts.

**Concrete numbers (klein 4B path):** launch 300 shipped × 5 candidates = 1,500 gens × ~$0.015 ≈ **$23 one-time**; steady state 10–50/week × 5 ≈ **$0.75–$3.80/week** (~$3–16/month). Schnell path: launch ≈ $1–5; weekly ≈ pennies. Upscaling: $0. No subscription, no idle GPU cost, no ops.

**Fallback: self-host FLUX.1-schnell (Apache 2.0 — the one fully-confirmed-clean license) either on the dev's own Apple Silicon (GGUF Q6 on 16–32GB, ~1.5–3 min/image, $0 marginal, run overnight) or on a rented RTX 4090 (~$0.40–0.67/hr; entire launch batch ≈ $3–10).** This hedges against API price/terms changes with zero licensing risk.

**Avoid:** self-hosting FLUX.1-dev commercially without a BFL license (model use prohibited [CONFIRMED]); buying a GPU or a Mac for this volume (payback never happens); Midjourney as pipeline (no sanctioned automation, non-exclusive perpetual license to MJ, public-by-default — all [UNVERIFIED] but uniformly unfavorable); relying on gpt-image-1 alone (1024x1536 ceiling forces heavier upscaling from a lower base).

**Licensing verdicts:** outputs commercially usable via — FLUX.1-schnell self-host [CONFIRMED], FLUX.1-dev outputs even under NC weights license [CONFIRMED], SD3.5 under $1M revenue with output ownership [CONFIRMED], BFL/OpenRouter hosted APIs (provider-licensed; reasonable inference, per-provider ToS [UNVERIFIED]). No AI image gets per-image US copyright [CONFIRMED] — plan the moat accordingly. Pre-generated catalogs appear exempt from Google Play's AI-GC policy [UNVERIFIED — re-check before launch].

**Conflicts in the digest (both sides unverified — do not rely on either):** claim 13 says Midjourney has a limited-release API (April 2026) while claims 18/106 say no public API and automated access prohibited; claim 52 notes an internal $0.03 vs $0.055 inconsistency for FLUX.2 [pro] in one comparison page (the [CONFIRMED] BFL first-party figure — $0.03 first MP + $0.015/MP — supersedes it).

---

## Refuted claims (excluded from all recommendations)

- **Claim 29** — "Commercial self-hosting of FLUX open weights is sold via paid license tiers rather than being free: Builder (klein, 10K img/mo), Professional (FLUX.2 dev, 100K img/mo), Enterprise — implying FLUX.2 [dev] self-hosted output is not commercially licensed without purchasing a tier." **[REFUTED — excluded]** Two votes refuted it (one vote confirmed only the tier-structure facts, which are real on https://bfl.ai/pricing, and noted an omitted "Platform" tier): (1) "rather than being free" is false as a generalization — the FLUX.2-klein-4B HuggingFace license evidence contradicts it (klein appears freely licensed); (2) the inference that FLUX.2 [dev] self-hosted *outputs* are not commercially licensed without a paid tier is contradicted by primary sources — consistent with the [CONFIRMED] FLUX.1-dev pattern where outputs are commercially usable even when commercial *model* use is restricted. Takeaway: BFL's paid tiers price commercial *model deployment* and volume, not output ownership; and klein-class weights may be free — but exact FLUX.2 klein/dev self-host terms remain [UNVERIFIED] here, which is why the primary recommendation uses klein via API and keeps the fully-confirmed Apache 2.0 schnell as the self-host fallback.
