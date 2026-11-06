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

def inspect(reference: Path, actual: Path, output: Path) -> dict:
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
        diff = ImageChops.difference(ref, img)
        stat = ImageStat.Stat(diff)
        mae = sum(stat.mean) / len(stat.mean)
        threshold = 12
        histogram = diff.convert("L").histogram()
        match = sum(histogram[:threshold + 1]) / (ref.width * ref.height)
        enhanced = ImageEnhance.Contrast(diff).enhance(3)
        enhanced.save(output.with_suffix(".png"))
        report = {
            "reference": str(reference),
            "actual": str(actual),
            "width": ref.width,
            "height": ref.height,
            "mae_rgb": round(mae, 3),
            "similar_pixels_tolerance_12": round(match, 5),
            "strictly_identical": diff.getbbox() is None,
            "diff_file": str(output.with_suffix(".png")),
            "note": "Cameras show different live scenes; compare overlays with matched "
                    "source imagery or masks before attributing scene differences to UI."
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
    args = parser.parse_args()
    print(json.dumps(inspect(args.reference, args.actual, args.output),
        ensure_ascii=False, indent=2))

if __name__ == "__main__":
    main()
