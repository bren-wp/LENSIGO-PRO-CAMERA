"""Checks 19-reference completeness and honest failure reporting."""
import tempfile
import unittest
from pathlib import Path
from PIL import Image
from pixel_audit_batch import SCREENS, run

class BatchPixelAuditTest(unittest.TestCase):
    def test_missing_screen_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            for name in ("ref", "actual"):
                (root / name).mkdir()
            Image.new("RGB", (10, 20), (0, 20, 30)).save(root / "ref" / SCREENS[0])
            Image.new("RGB", (10, 20), (0, 20, 30)).save(root / "actual" / SCREENS[0])
            report = run(root / "ref", root / "actual", root / "out")
            self.assertEqual(report["checked"], 1)
            self.assertEqual(len(report["missing"]), 18)
            self.assertFalse(report["all_screens_identical"])

    def test_all_matching_screens_pass(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            for name in ("ref", "actual"):
                (root / name).mkdir()
                for screen in SCREENS:
                    Image.new("RGB", (10, 20), (1, 30, 80)).save(root / name / screen)
            report = run(root / "ref", root / "actual", root / "out")
            self.assertEqual(report["identical"], 19)
            self.assertTrue(report["all_screens_identical"])

    def test_real_difference_is_not_reported_as_identical(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            for name in ("ref", "actual"):
                (root / name).mkdir()
                for screen in SCREENS:
                    color = (50, 60, 70) if name == "ref" else (51, 60, 70)
                    Image.new("RGB", (10, 20), color).save(root / name / screen)
            report = run(root / "ref", root / "actual", root / "out")
            self.assertEqual(report["checked"], 19)
            self.assertFalse(report["all_screens_identical"])

if __name__ == "__main__":
    unittest.main()
