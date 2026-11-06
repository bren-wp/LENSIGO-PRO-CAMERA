# LENSIGO — Pixel audit (real device screenshots)

Goal: verify each of 19 mobile and 19 tablet layouts against their corresponding user-supplied reference PNG.

Run on a device/emulator with the matching viewport and density, the same font scale (1.0), and consistent system bars. Capture actual screens with Android `adb shell screencap -p /sdcard/lensigo.png` then `adb pull /sdcard/lensigo.png`.

A reference photo is not a valid background substitute for a live sensor feed. For camera screens compare header, icons, HUD, lower controls, frames, safe insets, text, colors and positions separately from live photo content.

Install local audit dependency: `python -m pip install Pillow`.

```bash
python tools/pixel_audit.py --reference reference/mobitel/10_panorama.png --actual screenshots/panorama.png --output audit/10_panorama
```

The script refuses differing dimensions and produces a JSON report and intensified pixel-diff PNG. For an honest 1:1 claim the generated report must show `strictly_identical: true` on the appropriate deterministic/overlaid regions. Metrics for all 38 screens have not yet been recorded because there are no verified Android device screenshots. Do not use the design mockups as real app screenshots in README or Play assets.

## Reference features

- `izvoz/mobitel` 19 PNG files, 1080×2400 pixels
- `izvoz/tablet` 19 PNG files, 1600×2560 pixels
- Referentna launch ikonica: `01_ikona_512x512.png`
- Brending: `04_google_play_feature_1024x500.png`

Assets were provided by the user in conversation. Only text-based vector approximations have been committed; pixel-equivalent PNGs are not yet integrated in the Android resource tree. Launcher icon requires final device-level visual comparison.
