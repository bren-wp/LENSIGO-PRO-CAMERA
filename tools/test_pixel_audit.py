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
