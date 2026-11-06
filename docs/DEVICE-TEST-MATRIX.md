# LENSIGO — device/visual test matrix

The feature is **not considered device-tested** just because it compiles.

| Test configuration | Required checks | Result |
|---|---|---|
| Android 7 / API 24, 360 dp narrow | permissions incl. storage; photo JPEG; SD video; gallery ownership; controls not clipped | Not run |
| Android 9 / API 28, low RAM | noise reduction, portrait fallback, panorama 2–5 shots, release buffers on exit | Not run |
| Android 10 / API 29 | MediaStore owner paths, deleting owned media, app preferences | Not run |
| Android 12 / API 31 | camera live effects, Night/Bokeh extensions only when supported | Not run |
| Android 13–16 / API 33–36 | scoped media, edge-to-edge bars, system permission roundtrip | Not run |
| 1080 × 2400 pixel phone | compare 19 screens to `izvoz/mobitel` | No screenshots yet |
| 1600 × 2560 pixel tablet | compare 19 screens to `izvoz/tablet` | No screenshots yet |
| Landscape and large text | scrollability, no truncated buttons, 1.3×/2× font scaling | Not run |

## Screens 18 and 19

- Privacy: navy surface, centered cyan aperture, headline, four information rows, secondary information panel.
- About: centered symbol / LENSIGO wordmark, four rows (app name, website, privacy, licences), copyright and version.
- Buttons are wired to Android application settings, in-app privacy / licence information, or browser via ACTION_VIEW.
- All screen copies use meaningful icons and avoid representing future features as complete.

## Security and stability checks

- No INTERNET permission, third-party analytics or advertising SDK.
- Avoid broad media read permissions; the gallery selects LENSIGO file prefix and known MediaStore folder (Android 10+).
- On OS-settings resume, refresh microphone/camera/storage permission state.
- Cancel countdown on changing camera mode or leaving the camera, preventing delayed capture.
- Delete through Android's confirmation request on API 30+.
- Close streams, recycle decoded bitmaps, remove partial output on errors.
- No automatic backups for local app preferences (android:allowBackup=false).
- Manual tests for permission revocation, app backgrounding, slow storage, low storage, camera unavailable, camera switching, and vendor extension fallbacks remain necessary.

A screenshot score is valid only for the specific device settings used to capture the screenshot, and camera-content pixels should be masked when comparing overlay geometry. No general claim of pixel-for-pixel visual equivalence is made until per-screen reports are recorded.
