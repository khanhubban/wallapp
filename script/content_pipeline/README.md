# content_pipeline generate script

Operator-run Python helper for the StillScenes content pipeline: generate candidate
images (BFL FLUX), curate + upscale them into `masters/`, render the shipped
WebP renditions, and emit the `manifest.json` that the Kotlin
`:service:content-pipeline` module builds, validates, and publishes.

This directory is plain Python (no Gradle). The only non-stdlib deps are
`pillow` and `requests`, both already available in this environment.

## Workflow

### 0. Requirements

- `BFL_API_KEY` — API key for the BFL FLUX image generation API. **Never commit
  this.** Export it in your shell profile or pass it inline per-invocation:
  ```bash
  export BFL_API_KEY="..."
  ```
  `generate_candidates` reads it from the environment (`os.environ["BFL_API_KEY"]`)
  and raises if it's unset. `requests` is imported inside that function only, so
  importing `generate` or running the test suite never touches the network and
  never needs the key.
- `realesrgan-ncnn-vulkan` on your `PATH` (for upscaling candidates).
- An `rclone` remote configured for the Cloudflare R2 account that hosts the
  `stillscenes-masters` bucket (the Kotlin publisher already uses a remote
  named `r2staging` for the renditions bucket — reuse the same remote here
  unless you've set up a separate one for masters).

### 1. Generate candidates

```python
from generate import generate_candidates
generate_candidates(["prompt describing scene..."], out_dir="candidates/")
```

Calls the real BFL FLUX.2 [klein] API and polls until each image is ready,
writing `candidates/cand_<i>.png`. This is operator-run only — it is not unit
tested (no network access in tests) and the BFL endpoint/response shape should
be treated as best-effort until verified against the live API on first real
call.

### 2. Curate

Manually review `candidates/*.png` and pick winners. Copy each chosen image to
`masters/<wallpaper-id>/original.png` (the master archive — keep the
full-resolution source in case you need to re-render or re-upscale later).

### 3. Upscale

Upscale each curated master 2x with Real-ESRGAN:

```bash
realesrgan-ncnn-vulkan -n realesrgan-x2plus -i masters/<id>/original.png -o masters/<id>/upscaled.png
```

### 4. OLED dark-gradient eyeball check

StillScenes wallpapers are viewed full-screen on phone displays, many of them
OLED/AMOLED with true blacks. WebP at q92 can introduce visible banding in
dark gradients that isn't obvious at thumbnail size. Before rendering:

- View `masters/<id>/upscaled.png` at 100% zoom on an OLED/AMOLED screen if
  you have one (or a display capable of true blacks).
- Focus on the darkest gradient regions (sky-to-horizon fades, shadow
  falloffs) and look for visible banding/posterization steps rather than a
  smooth gradient.
- If banding is visible, regenerate or re-grade the source before it goes
  into a rendition — `render_webp` uses `method=6` to minimize (not
  eliminate) banding, so a master with existing banding will still show it.

### 5. Render renditions + build the manifest

```python
from generate import build_manifest, render_webp, RENDITION_MAX

wallpapers = [
    {
        "id": "stillscenes_1a2b3c4d",
        "label": "Aurora 01",
        "is_dark": True,
        "width": 1440,
        "height": 3120,
        "tags": ["dark"],
        "styles": ["amoled"],
        "colors": ["dark"],
    },
    # ...one entry per curated wallpaper
]

out_dir = "dist/20260709-01"  # manifest.json lives here; media/ is a sibling

for w in wallpapers:
    src = f"masters/{w['id']}/upscaled.png"
    render_webp(src, f"{out_dir}/media/{w['id']}/download.webp", *RENDITION_MAX["download"])
    render_webp(src, f"{out_dir}/media/{w['id']}/preview.webp", *RENDITION_MAX["preview"])

# Also render artist/folder profile + banner images to:
#   {out_dir}/media/artist/stillscenes/profile.webp
#   {out_dir}/media/folder/justadded/profile.webp
#   {out_dir}/media/folder/justadded/banner.webp

manifest = build_manifest("20260709-01", "https://media-staging.stillscenes.app", wallpapers)

import json
with open(f"{out_dir}/manifest.json", "w") as f:
    json.dump(manifest, f, indent=2)
```

**Layout matters:** the Kotlin publisher (`Main.kt`) resolves every rendition
path in the manifest (`downloadRenditionPath`, `previewRenditionPath`,
`profileImagePath`, `featureBannerImagePath`) relative to the directory that
contains `manifest.json`. Keep `media/` as a sibling of `manifest.json`, exactly
as built above, or the publish step will fail to find the files.

### 6. Upload masters/ to the archive bucket

Archive the full-resolution masters (pre-render, post-upscale) to the
`stillscenes-masters` R2 bucket so they can be re-processed later without
re-running generation:

```bash
rclone copy masters/ r2staging:stillscenes-masters/$(date +%Y%m%d)/ --progress
```

(Swap `r2staging` for your local remote name if it differs — see Requirements
above.)

### 7. Run the Kotlin build → validate → publish pipeline

```bash
./gradlew :service:content-pipeline:run --args="dist/20260709-01/manifest.json"
```

This builds the catalog/search/media wire bundle from the manifest, validates
referential integrity, publishes to the CDN-backed staging bucket with a
read-back check, and prints the version to flip the RC `catalog_version` to
once you're satisfied it's live.

## Testing

The pure functions (`build_manifest`, `render_webp`) are unit tested with
stdlib `unittest` (no pytest in this environment):

```bash
cd script/content_pipeline && python3 -m unittest test_generate.py -v
```

`generate_candidates` is intentionally untested — it's a thin, operator-run
wrapper around a real network API.
