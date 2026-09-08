# Launch Hardening Ledger — Limited Android MVP

Branch: `feat/foundation-delivery` @ `828c160`. No CI (`.github/workflows/` absent).
Untracked (never commit): `.wrangler/`, `learn/`, `node_modules/`. `.gitignore` covers none of them.
Authoritative context: `docs/HANDOFF.md`, `.superpowers/sdd/progress.md`, ship-readiness spec+plan.

## Blocker 1 — Rotate leaked Cloudflare + BFL credentials
- Evidence: HANDOFF says CF scoped token + BFL key were pasted into chat and are live; rotation is the user's task.
- Affected: Cloudflare dashboard token; BFL provider console key; consumers are manual `curl`/scripts (nothing committed reads them — `CF_TOKEN`/`BFL_API_KEY` env absent in this shell).
- Status: REPO/HISTORY CLEAN — length-only scan of commit `e77697e` shows the sole `cfut_` match is the bare 5-char prefix (no usable value); current tree mentions only redacted `cfut_…` in `docs/HANDOFF.md`. Exposure is chat-only.
- Action: USER must (1) revoke old CF scoped token, mint new with Cache-Rules Edit on the zone, export locally/CI secret only; (2) regenerate BFL key, same handling. No history rewrite needed once revoked (no usable value in history).
- Verification: new-token `curl` against CF API succeeds; old token rejected. NOT DONE — no new token available here.
- Remaining risk: until revoked, leaked tokens are usable by anyone who saw them. **BLOCKED ON USER.**

## Blocker 2 — Prod CDN caching (Task 5)
- Evidence (2026-07-10): staging `HIT`, prod `DYNAMIC`. Re-verified just now (this session): **BOTH hosts `DYNAMIC`** on `GET /api/20260709-06/content-1a` (`HTTP 200`, `server: cloudflare`). Staging regressed — the staging-only cache rule is gone or stopped matching.
- Affected: Cloudflare `http_request_cache_settings` entrypoint, zone from HANDOFF; both R2 custom domains.
- Action: GET ruleset, PUT back union of existing + prod-covering rule (single expression over both hosts, `/api/` prefix + webp eligibility), re-test both hosts to `HIT`.
- Verification: repeat `curl -sI` showing `cf-cache-status: HIT` on both hosts. NOT DONE — needs rotated CF token (Blocker 1). **BLOCKED ON USER.**
- Remaining risk: every prod + staging request currently bills R2 Class B; launch traffic = cost spike.

## Blocker 3 — Release-build device smoke vs prod: FAILED (new P0 found, user action required)
- Evidence run 1: `:app:android:assembleWallAppRelease` BUILD SUCCESSFUL; APK verified (`app.stillscenes`, v1.3.4, signer `65355edc…` = registered Firebase SHA-1); `adb install` Success; `am start` OK. First launch crashed: `Process: app.stillscenes … o9.k: An operation is not implemented: Replace with your production Rewarded Ad Unit ID` (`AdUnitIds.android.kt:21,37,45,52` — four release-branch TODOs read eagerly at Koin construction, `Factory.android.kt:756`).
- Evidence run 2 (fix attempt): replaced release TODOs with `""` matching the file's sibling `""` pattern — REJECTED by verification. Release then crashed in `MainActivity.onCreate` → Koin `Could not create instance` chain → `IllegalArgumentException: Failed requirement`: the ID value classes (`RewardAdUnitId.kt:7`, `InlineAdUnitId.kt:5`, `AppOpenAdUnitId.kt:6`) `require(id.isNotEmpty())`. Empty is not a legal disable signal. Reverted exactly (`git diff` on the file is empty).
- Latent: `rewardInterstitialAdUnitId`/`interstitialAdUnitId` return `""` in ALL builds — same `require` will throw if those paths are ever read. Unread today.
- `Main.kt:99` FIXED (only tree change outside ledger/CI): message now reads "Ensure RC <key> is <version> … (no flip needed if it already is)". No test covered the string.
- Action: USER must create AdMob app for `app.stillscenes` + 4 units (rewarded, app-open, feed native, feed native-video) and paste the real IDs into the four `else` branches; then rebuild + re-smoke. No test IDs in release (AdMob policy).
- Verification: installed release starts with zero FATAL, loads `api/20260709-06` from `media.stillscenes.app`, renders, sign-in succeeds. NOT DONE — blocked on AdMob IDs.
- Remaining risk: release has never run past DI startup; catalog/render/sign-in on release all still unverified.

## Blocker 4 — Minimal CI + merge PR #1
- Evidence: PR #1 open, no CI; HANDOFF gate = 9× `desktopTest` + `:service:content-pipeline:test` with genuine rerun semantics (repeat `--rerun` per task, check JUnit `mtime`).
- Action: add `.github/workflows/ci.yml` (JDK 17, HANDOFF tasks), quarantine the two known-broken modules explicitly, verify from clean checkout, merge PR #1.
- Status: DRAFTED `.github/workflows/mvp-gate.yml` (ruby-YAML-validated); all 10 gate module paths confirmed in `settings.gradle.kts`; `--rerun-tasks` for genuine execution; 3 quarantined modules named in-file. NOT yet run (Gradle busy with release build), NOT merged.
- Verification: CI green on branch; clean-checkout run observed. NOT DONE.

## Blocker 5 — RC rollback drill
- Evidence: live RC versions 1–5 exist (`versions:list` observed this session); v5 current, v4 documented rollback target; both name `catalog_version=20260709-06`, so v5→v4 is behaviorally near-no-op (only drops `catalog_version_staging`, whose compiled-in default equals the value).
- Action: fetch v4 template, diff vs live, `remoteconfig:rollback --version-number 4`, verify served values + app fetch, roll forward to v5 copy, verify again.
- Status: PREPPED read-only — `/tmp/rc-v4.json` vs `/tmp/rc-live.json` differ ONLY by `catalog_version_staging` (absent in v4, `20260709-06` in live); all other defaults identical. Rollback is behaviorally near-no-op (debug falls back to compiled-in default = same value).
- Verification: before/after `remoteconfig:get` snapshots + app fetch logs. NOT DONE (prod writes — run after Blockers 3–4 per order).
- Remaining risk: full-template replace semantics; drill itself is the mitigation proof.
