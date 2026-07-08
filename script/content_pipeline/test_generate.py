import os
import tempfile
import unittest

from generate import build_manifest, render_webp


class TestBuildManifest(unittest.TestCase):
    def test_build_manifest_shape(self):
        wps = [{
            "id": "stillscenes_1a2b3c4d",
            "label": "Aurora 01",
            "is_dark": True,
            "width": 1440,
            "height": 3120,
            "tags": ["dark"],
            "styles": ["amoled"],
            "colors": ["dark"],
        }]
        m = build_manifest("20260708-01", "https://media-staging.stillscenes.app", wps)

        self.assertEqual(m["version"], "20260708-01")
        w = m["wallpapers"][0]
        self.assertEqual(w["downloadRenditionPath"], "media/stillscenes_1a2b3c4d/download.webp")
        self.assertEqual(w["previewRenditionPath"], "media/stillscenes_1a2b3c4d/preview.webp")
        self.assertEqual(m["artist"]["id"], "stillscenes")
        self.assertEqual(m["folder"]["id"], "f~justadded")


class TestRenderWebp(unittest.TestCase):
    def test_render_webp_strips_metadata_and_fits_max(self):
        from PIL import Image

        with tempfile.TemporaryDirectory() as tmp_dir:
            src = os.path.join(tmp_dir, "master.png")
            Image.new("RGB", (2000, 4000), (10, 10, 10)).save(src)
            out = os.path.join(tmp_dir, "download.webp")

            render_webp(str(src), str(out), max_w=1440, max_h=3120, quality=92)

            with Image.open(out) as im:
                im.load()
                self.assertLessEqual(im.width, 1440)
                self.assertLessEqual(im.height, 3120)
                self.assertFalse(im.info.get("exif"))
                self.assertFalse(im.info.get("icc_profile"))


if __name__ == "__main__":
    unittest.main()
