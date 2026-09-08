#!/usr/bin/env python3
"""Reconstruct a pipeline manifest from a published catalog, for the Task 10 Step 5 byte-identity proof.

Why this exists
---------------
Manifests are not kept in this repo. Step 5 must rebuild a published catalog with the current builder
and prove the bytes are unchanged. It turns out the manifest is not needed: everything the *media map*
depends on is recoverable from the catalog itself.

    MediaMapBuilder keys every entry by mediaId("<id>:<role>"), and
    mediaId(seed) = SHA-256(seed)[0:7] as a 56-bit big-endian Long   (see build/MediaIds.kt)

The seeds are built from the artist id, the folder id, and the wallpaper ids -- all of which content-1a
publishes. Only the three "chrome" paths (artist profile, folder profile, folder banner) have to be read
back out of the live media map, because `media/folder/justadded/...` is not derivable from the folder id
`f~justadded`.

What this does and does not prove
---------------------------------
Feeding the output back through the builder and comparing raw bytes proves the builder preserves the key
set, the key order, and mediaId coverage -- the entire surface the Phase 3 refactor touched. Key order is
NOT copied from the live map; it comes from MediaEntityKind, so it stays independently derived.

It does NOT prove this is the manifest that originally produced the catalog. Fields that never reach the
media map (label, tags, colors, isDark, width, height) are unconstrained by the comparison.

It also cannot be used to publish: the non-dry-run path in Main.kt resolves renditions as
File(manifestDir, renditionPath), so the real .webp files must exist beside a real manifest.

Usage
-----
    python3 reconstruct_staging_manifest.py > staging-manifest.json
    python3 reconstruct_staging_manifest.py --version 20260709-06 --base https://media-staging.stillscenes.app

Then, from the repo root:

    ./gradlew :service:content-pipeline:run --args="$PWD/staging-manifest.json --dry-run" -q \
      | grep '^{' > rebuilt.json
    curl -sS "$BASE/api/$VERSION/media-1a-c-p~s" > live.json
    cmp <(printf '%s' "$(cat rebuilt.json)") <(printf '%s' "$(cat live.json)") && echo BYTE-IDENTICAL

Compare raw bytes. Do NOT normalize with `json.tool --sort-keys`: sorting keys hides a reordered wire
format, which is the single regression this proof exists to catch.
"""
import argparse
import collections
import hashlib
import json
import sys
import urllib.request

MEDIA_MAP_KEY = "media-1a-c-p~s"


def media_id(seed: str) -> int:
    """Port of build/MediaIds.kt: top 7 bytes of SHA-256, big-endian, always positive."""
    digest = hashlib.sha256(seed.encode()).digest()
    value = 0
    for i in range(7):
        value = (value << 8) | digest[i]
    return value


def fetch_json(url: str):
    # Cloudflare 403s the default "Python-urllib/3.x" User-Agent on these hostnames. curl works, urllib
    # does not, and the failure looks like a permissions problem rather than a UA problem. Send one.
    request = urllib.request.Request(url, headers={"User-Agent": "stillscenes-pipeline-tools/1.0"})
    with urllib.request.urlopen(request, timeout=30) as response:
        raw = response.read().decode()
    return json.loads(raw, object_pairs_hook=collections.OrderedDict)


def build(base: str, version: str) -> dict:
    root = f"{base.rstrip('/')}/api/{version}"
    content = fetch_json(f"{root}/content-1a")
    media = fetch_json(f"{root}/{MEDIA_MAP_KEY}")["data"]

    def path_for(seed: str) -> str:
        """Resolve a rendition path by RECOMPUTED mediaId, never by position."""
        entry = media[str(media_id(seed))]  # KeyError if the seed is wrong -- fail loud
        urls = set(entry.values())
        if len(urls) != 1:
            sys.exit(f"{seed}: expected one url across its keys, got {sorted(urls)}")
        url = urls.pop()
        prefix = base.rstrip("/") + "/"
        if not url.startswith(prefix):
            sys.exit(f"{seed}: url {url} is not under {prefix}")
        return url[len(prefix):]

    artist, folder = content["artists"][0], content["folders"][0]
    manifest = {
        "version": version,
        "baseUrl": base.rstrip("/"),
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
            }
            for w in content["wallpapers"]
        ],
    }

    # The media map is a LinkedHashMap; its insertion order is manifest order. If content-1a listed the
    # wallpapers differently, the rebuilt JSON would differ only in entry order -- catch that here rather
    # than in a confusing byte diff.
    expected = []
    for w in content["wallpapers"]:
        expected += [str(media_id(f"{w['id']}:download")), str(media_id(f"{w['id']}:preview"))]
    expected += [
        str(media_id(f"{artist['id']}:profile")),
        str(media_id(f"{folder['id']}:profile")),
        str(media_id(f"{folder['id']}:banner")),
    ]
    if list(media.keys()) != expected:
        sys.exit(f"entry-order mismatch\n live: {list(media.keys())}\n mine: {expected}")

    print(f"reconstructed {version}: {len(manifest['wallpapers'])} wallpapers, "
          f"{len(media)} media entries, order matches live", file=sys.stderr)
    return manifest


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    parser.add_argument("--base", default="https://media-staging.stillscenes.app")
    parser.add_argument("--version", default="20260709-06")
    args = parser.parse_args()
    json.dump(build(args.base, args.version), sys.stdout, indent=2)
    sys.stdout.write("\n")


if __name__ == "__main__":
    main()
