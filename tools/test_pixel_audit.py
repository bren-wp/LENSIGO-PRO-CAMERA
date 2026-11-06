"""Small deterministic tests for LENSIGO's pixel-diff report."""
import tempfile
import unittest
from pathlib import Path
from PIL import Image
from pixel_audit import inspect

class PixelAuditTest(unittest.TestCase):
    def test_exact_and_different_images(self):
        with tempfile.TemporaryDirectory() as directory:
            p = Path(directory)
            a, b = p / "a.png", p / "b.png"
            Image.new("RGB", (6, 8), (17, 28, 35)).save(a)
            Image.new("RGB", (6, 8), (17, 28, 35)).save(b)
            exact = inspect(a, b, p / "exact")
            self.assertTrue(exact["strictly_identical"])
            self.assertEqual(exact["similar_pixels_tolerance_12"], 1.0)
            Image.new("RGB", (6, 8), (244, 22, 1)).save(b)
            changed = inspect(a, b, p / "changed")
            self.assertFalse(changed["strictly_identical"])
            self.assertTrue((p / "changed.png").is_file())

    def test_ignore_only_dynamic_scene_does_not_claim_entire_screen_is_identical(self):
        with tempfile.TemporaryDirectory() as directory:
            p = Path(directory)
            a, b = p / "a.png", p / "b.png"
            Image.new("RGB", (8, 8), (17, 28, 35)).save(a)
            changed = Image.new("RGB", (8, 8), (17, 28, 35))
            changed.paste((200, 200, 200), (0, 0, 3, 3))
            changed.save(b)
            report = inspect(a, b, p / "masked", ignored_rects=[(0, 0, 3, 3)])
            self.assertFalse(report["strictly_identical"])
            self.assertTrue(report["compared_regions_identical"])
            self.assertEqual(report["ignored_pixels"], 9)
            self.assertEqual(report["compared_pixels"], 55)
            self.assertEqual(report["mae_rgb"], 0.0)

    def test_ignore_rectangles_must_leave_valid_pixels(self):
        with tempfile.TemporaryDirectory() as directory:
            p = Path(directory)
            a = p / "a.png"
            Image.new("RGB", (8, 8), (17, 28, 35)).save(a)
            for rectangle in [(0, 0, 8, 8), (0, 0, 9, 8), (-1, 0, 3, 3)]:
                with self.assertRaises(ValueError):
                    inspect(a, a, p / "masked", ignored_rects=[rectangle])

    def test_mismatched_screenshot_dimensions_are_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            p = Path(directory)
            a, b = p / "a.png", p / "b.png"
            Image.new("RGB", (10, 10)).save(a)
            Image.new("RGB", (10, 12)).save(b)
            with self.assertRaises(ValueError):
                inspect(a, b, p / "diff")

if __name__ == "__main__":
    unittest.main()
