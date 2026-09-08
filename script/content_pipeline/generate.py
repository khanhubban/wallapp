# pip install pillow requests
import hashlib, os, sys, time
from PIL import Image

RENDITION_MAX = {"download": (1440, 3120), "preview": (1080, 2160)}  # preview fits p~five0

def rid(seed: str) -> str:
    return "stillscenes_" + hashlib.sha256(seed.encode()).hexdigest()[:8]

def build_manifest(version: str, base_url: str, wallpapers: list) -> dict:
    wps = []
    for w in wallpapers:
        wid = w["id"]
        wps.append({
            "id": wid, "label": w["label"], "isDark": bool(w["is_dark"]),
            "width": w["width"], "height": w["height"],
            "downloadRenditionPath": f"media/{wid}/download.webp",
            "previewRenditionPath": f"media/{wid}/preview.webp",
            "styles": w.get("styles", []), "tags": w.get("tags", []), "colors": w.get("colors", []),
        })
    return {
        "version": version, "baseUrl": base_url,
        "artist": {"id": "stillscenes", "label": "StillScenes", "profileImagePath": "media/artist/stillscenes/profile.webp"},
        "folder": {"id": "f~justadded", "title": "Just Added",
                   "profileImagePath": "media/folder/justadded/profile.webp",
                   "featureBannerImagePath": "media/folder/justadded/banner.webp"},
        "wallpapers": wps,
    }

def render_webp(src_path: str, out_path: str, max_w: int, max_h: int, quality: int = 92) -> None:
    im = Image.open(src_path).convert("RGB")           # drop alpha; opaque
    im.thumbnail((max_w, max_h), Image.LANCZOS)        # fit within max, preserve aspect
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    # No exif/icc passed → metadata stripped. q92 + method=6 to limit dark-gradient banding.
    im.save(out_path, "WEBP", quality=quality, method=6)

def generate_candidates(prompts: list, out_dir: str) -> list:
    """Real BFL FLUX.2 [klein] async calls. Isolated so unit tests don't hit the network.
    Endpoint verified 2026-07-09: api.bfl.ai/v1/flux-2-klein-4b + x-key auth + {prompt,width,height}
    body are valid (a live call returned 402 credits-only, so the request shape passed). The
    submit -> polling_url -> poll -> result.sample flow follows BFL docs; confirm on the first
    *credited* run."""
    import requests
    key = os.environ["BFL_API_KEY"]
    results = []
    for i, prompt in enumerate(prompts):
        # 768x1344 portrait is verified-valid (tune toward the phone aspect / BFL max as needed).
        r = requests.post("https://api.bfl.ai/v1/flux-2-klein-4b",
                          headers={"x-key": key},
                          json={"prompt": prompt, "width": 768, "height": 1344}).json()
        poll = r["polling_url"]
        for _ in range(120):                            # ~60s; sample URL expires in 10 min
            time.sleep(0.5)
            res = requests.get(poll, headers={"x-key": key}).json()
            status = res.get("status")
            if status == "Ready":
                url = res["result"]["sample"]
                img = requests.get(url).content         # download within the 10-min window
                p = os.path.join(out_dir, f"cand_{i}.png"); open(p, "wb").write(img); results.append(p); break
            if status in ("Error", "Failed", "Content Moderated"):
                print(f"skip prompt {i}: {status}", file=sys.stderr); break
            if res.get("status") == "Request Moderated":
                print(f"skip prompt {i}: moderated", file=sys.stderr); break
        # 429 handling: requests could add capped backoff here if r.status_code == 429
    return results

# Curation is manual: you copy chosen candidates to masters/<id>/upscaled.png,
# upscale with `realesrgan-ncnn-vulkan -n realesrgan-x2plus -i cand.png -o upscaled.png`,
# then run this to render + emit manifest. See README.md.
if __name__ == "__main__":
    print("See README.md — curate masters/, then call build_manifest + render_webp per wallpaper.")
