#!/usr/bin/env python3
"""Stage the first prod publish (Task 11): build a prod manifest and fetch its renditions locally.

Writes nothing remote. Everything here is GETs against the staging CDN plus local file writes.

Why a staging step exists at all
-------------------------------
The prod catalog is the staging catalog served from a different host. `Main.kt` derives the target R2
bucket from the manifest's `baseUrl` -- there is deliberately no `--bucket` flag -- so swapping `baseUrl`
to https://media.stillscenes.app is the whole of "publish to prod".

But a manifest alone cannot publish. `Main.kt`'s non-dry-run path resolves every rendition as
`File(manifestDir, renditionPath)`, and the prod bucket is empty, so the publish uploads the .webp images
too. Those files are byte-identical across environments and re-downloadable from the staging CDN. This
script lays them out under the manifest directory exactly as Main.kt expects.

Usage
-----
    python3 stage_prod_publish.py /abs/path/to/stagedir
    ./gradlew :service:content-pipeline:run --args="/abs/path/to/stagedir/manifest.json --dry-run"

The dry run must print `bucket stillscenes-content-prod`. Read that line before doing anything else.

Then, and only with explicit authorization, drop `--dry-run`. That writes 32 objects:
11 renditions, 18 media-map copies (2 platforms x 9 size classes), content-metadata-1a, spec.json, and
content-1a LAST -- so a partial upload never activates a half-published catalog.

Note: `catalog_version` in Remote Config is ALREADY 20260709-06. The moment content-1a lands in the prod
bucket, release builds serve it. No RC flip is required afterwards, despite what Main.kt's success
message says.

A manifest produces THREE wire objects, not one. Verifying the media map is byte-identical says nothing
about the other two. `styles`/`tags`/`colors` reach only `content-metadata-1a`, via SearchBuilder; a
reconstruction that drops them still yields a byte-identical media map AND a byte-identical content-1a,
and quietly publishes an empty search index. That happened once. Always diff all three:

    for k in content-1a content-metadata-1a spec.json; do
      cmp <(curl -sS "$PROD/api/$V/$k") <(curl -sS "$STAGING/api/$V/$k") && echo "identical $k"
    done
    # media-1a-c-p~s differs from staging ONLY by the host substring
"""
import argparse
import hashlib
import json
import os
import sys
import urllib.request

STAGING = "https://media-staging.stillscenes.app"
PROD = "https://media.stillscenes.app"
MEDIA_MAP_KEY = "media-1a-c-p~s"
# Cloudflare 403s the default "Python-urllib/3.x" User-Agent on these hostnames; curl works, urllib does
# not, and the failure reads as a permissions problem rather than a UA problem.
UA = {"User-Agent": "stillscenes-pipeline-tools/1.0"}


def fetch(url: str) -> bytes:
    with urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=60) as response:
        return response.read()


def media_id(seed: str) -> int:
    """Port of build/MediaIds.kt: top 7 bytes of SHA-256, big-endian."""
    digest = hashlib.sha256(seed.encode()).digest()
    value = 0
    for i in range(7):
        value = (value << 8) | digest[i]
    return value


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    parser.add_argument("stagedir", help="directory to write manifest.json + media/ into")
    parser.add_argument("--version", default="20260709-06")
    parser.add_argument("--source-base", default=STAGING, help="CDN to read the catalog and images from")
    parser.add_argument("--target-base", default=PROD, help="baseUrl to write into the manifest")
    args = parser.parse_args()

    root = f"{args.source_base}/api/{args.version}"
    content = json.loads(fetch(f"{root}/content-1a"))
    media = json.loads(fetch(f"{root}/{MEDIA_MAP_KEY}"))["data"]
    # styles/tags/colors reach content-metadata-1a via SearchBuilder and NOTHING else -- not content-1a,
    # not the media map. Omitting them still yields a byte-identical media map and a byte-identical
    # content-1a, and silently publishes an empty search index. Recover them from the search object.
    # SearchBuilder.entries() assigns relevance BY POSITION (1.0 - i*0.05, floored at 0.5), so the ordered
    # `t` values are exactly the manifest's lists. searchTerms is derived and regenerates itself.
    search = json.loads(fetch(f"{root}/content-metadata-1a"))
    terms = {
        r["remixId"]: {k: [e["t"] for e in r.get(k, [])] for k in ("styles", "tags", "colors")}
        for r in search["remixMetadata"]
    }

    def path_for(seed: str) -> str:
        """Resolve a rendition path by RECOMPUTED mediaId, never by position."""
        entry = media[str(media_id(seed))]  # KeyError if the seed is wrong -- fail loud
        urls = set(entry.values())
        if len(urls) != 1:
            sys.exit(f"{seed}: expected one url across its keys, got {sorted(urls)}")
        url = urls.pop()
        prefix = args.source_base.rstrip("/") + "/"
        if not url.startswith(prefix):
            sys.exit(f"{seed}: url {url} is not under {prefix}")
        return url[len(prefix):]

    artist, folder = content["artists"][0], content["folders"][0]
    manifest = {
        "version": args.version,
        "baseUrl": args.target_base.rstrip("/"),  # <-- selects the R2 bucket, via Main.kt bucketFor()
        "artist": {
            "id": artist["id"],
            "label": artist["label"],
            "profileImagePath": path_for(f"{artist['id']}:profile"),
        },
        "folder": {
            "id": folder["id"],
            "title": folder["title"],
            "profileImagePath": path_for(f"{folder['id']}:profile"),
            "featureBannerImagePath": path_for(f"{folder['id']}:banner"),
        },
        "wallpapers": [
            {
                "id": w["id"],
                "label": w["label"],
                "isDark": w["isDark"],
                "width": w["dlm"]["w"],
                "height": w["dlm"]["h"],
                "downloadRenditionPath": path_for(f"{w['id']}:download"),
                "previewRenditionPath": path_for(f"{w['id']}:preview"),
                "styles": terms[w["id"]]["styles"],
                "tags": terms[w["id"]]["tags"],
                "colors": terms[w["id"]]["colors"],
            }
            for w in content["wallpapers"]
        ],
    }

    missing_terms = [w["id"] for w in content["wallpapers"] if w["id"] not in terms]
    if missing_terms:
        sys.exit(f"no search metadata for {missing_terms}; would publish an empty search index")

    os.makedirs(args.stagedir, exist_ok=True)
    with open(os.path.join(args.stagedir, "manifest.json"), "w") as f:
        json.dump(manifest, f, indent=2)

    # Exactly the set, and the order, that Main.kt:90-96 builds.
    paths = []
    for w in manifest["wallpapers"]:
        paths += [w["downloadRenditionPath"], w["previewRenditionPath"]]
    paths += [
        manifest["artist"]["profileImagePath"],
        manifest["folder"]["profileImagePath"],
        manifest["folder"]["featureBannerImagePath"],
    ]

    print(f"staging {len(paths)} renditions into {args.stagedir}", file=sys.stderr)
    for p in paths:
        dest = os.path.join(args.stagedir, p)
        os.makedirs(os.path.dirname(dest), exist_ok=True)
        body = fetch(f"{args.source_base}/{p}")
        with open(dest, "wb") as f:
            f.write(body)
        if not (body[:4] == b"RIFF" and body[8:12] == b"WEBP"):
            sys.exit(f"{p}: not a WebP ({len(body)} bytes)")
        print(f"  {len(body):>8} bytes  {p}", file=sys.stderr)

    missing = [p for p in paths if not os.path.isfile(os.path.join(args.stagedir, p))]
    if missing:
        sys.exit(f"MISSING after download: {missing}")
    print(f"\nmanifest baseUrl = {manifest['baseUrl']}", file=sys.stderr)
    print("next: dry-run it and CHECK THE BUCKET LINE before publishing", file=sys.stderr)


if __name__ == "__main__":
    main()
