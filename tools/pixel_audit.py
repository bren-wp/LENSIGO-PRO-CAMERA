#!/usr/bin/env python3
"""LENSIGO screenshot-to-reference pixel audit.

Usage:
  python3 tools/pixel_audit.py --reference refs/mobitel/05_photo.png \
      --actual screenshots/05_photo.png --output artifacts/photo

Reference and actual must be screenshots of the SAME resolution, orientation,
font scale, status bar and navigation-bar configuration. This tool does not
silently resize the actual image because that could conceal mismatches.
"""
import argparse
import json
from pathlib import Path

from PIL import Image, ImageChops, ImageStat, ImageEnhance

def inspect(reference: Path, actual: Path, output: Path,
            ignored_rects=()) -> dict:
    with Image.open(reference) as ref_image, Image.open(actual) as actual_image:
        ref = ref_image.convert("RGB")
        img = actual_image.convert("RGB")
        if ref.size != img.size:
            raise ValueError(
                f"Resolution mismatch: reference {ref.size}, actual {img.size}. "
                "Set identical emulator/device size and density first."
            )
        if output.parent != Path("."):
            output.parent.mkdir(parents=True, exist_ok=True)
        # Full-frame differences remain visible in the report even if a
        # caller explicitly excludes moving sensor pixels from a UI-only audit.
        diff = ImageChops.difference(ref, img)
        mask = Image.new("L", ref.size, 255)
        display_diff = diff.copy()
        rectangles = []
        for candidate in ignored_rects:
            if len(candidate) != 4:
                raise ValueError("Ignore rectangles need four integers: x1 y1 x2 y2")
            x1, y1, x2, y2 = candidate
            if not (0 <= x1 < x2 <= ref.width and 0 <= y1 < y2 <= ref.height):
                raise ValueError("Ignore rectangle is outside the screenshot")
            rect = (x1, y1, x2, y2)
            rectangles.append(list(rect))
            mask.paste(0, rect)
            display_diff.paste((0, 0, 0), rect)
        pixels = mask.histogram()[255]
        if pixels == 0:
            raise ValueError("All pixels were excluded; no verifiable UI remains")
        stat = ImageStat.Stat(diff, mask=mask)
        mae = sum(stat.mean) / len(stat.mean)
        threshold = 12
        histogram = diff.convert("L").histogram(mask=mask)
        match = sum(histogram[:threshold + 1]) / pixels
        enhanced = ImageEnhance.Contrast(display_diff).enhance(3)
        enhanced.save(output.with_suffix(".png"))
        report = {
            "reference": str(reference),
            "actual": str(actual),
            "width": ref.width,
            "height": ref.height,
            "mae_rgb": round(mae, 3),
            "similar_pixels_tolerance_12": round(match, 5),
            "strictly_identical": diff.getbbox() is None,
            "compared_regions_identical": display_diff.getbbox() is None,
            "compared_pixels": pixels,
            "ignored_pixels": ref.width * ref.height - pixels,
            "ignored_rectangles": rectangles,
            "diff_file": str(output.with_suffix(".png")),
            "note": "Ignored rectangles are never verified. Compare all visible "
                    "HUD/controls outside masks separately; identical compared "
                    "regions are not proof of 1:1 full-screen fidelity."
        }
        output.with_suffix(".json").write_text(
            json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8"
        )
        return report

def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--reference", type=Path, required=True)
    parser.add_argument("--actual", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--ignore-rect", type=int, nargs=4, action="append",
                        metavar=("X1", "Y1", "X2", "Y2"), default=[],
                        help="Exclude a dynamic scene rectangle from UI-only metrics")
    args = parser.parse_args()
    print(json.dumps(inspect(args.reference, args.actual, args.output,
                             ignored_rects=args.ignore_rect),
        ensure_ascii=False, indent=2))

if __name__ == "__main__":
    main()
