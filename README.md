<p align="center"><img src="branding/lensigo-hero.svg" alt="LENSIGO — Pro Camera by Brendigo" width="100%"></p>

<h1 align="center">LENSIGO · PRO CAMERA</h1>
<p align="center"><strong>Svaki kadar. Tvoja priča.</strong><br>Every frame. Your story.<br><a href="https://brendigo.com">BY BRENDIGO</a></p>
<p align="center"><img alt="Android" src="https://img.shields.io/badge/Android-7.0%2B-00C8F5?style=for-the-badge"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.4.21-2E83FF?style=for-the-badge"><img alt="License" src="https://img.shields.io/badge/License-MIT-10364C?style=for-the-badge"></p>

> **Capture more. Create freely.** LENSIGO is a privacy-minded Android camera experience with an original deep-navy and cyan design, real local capture and a roadmap for professional tools.

## ✦ Glavne mogućnosti / Highlights

| 📷 Fotografije | 🎬 Video | 🔄 Kamere | 🔐 Privatnost |
|---|---|---|---|
| Kamera uživo i JPEG spremanje | Video s opcionalnim zvukom | Prednja i stražnja | MediaStore, bez računa |

**U početnoj verziji stvarno rade:** prikaz kamere uživo, fotografiranje, videosnimanje, prebacivanje kamere, dozvole, pristup galeriji i osnovne postavke. **Pro, Night, Portrait, Panorama, Dual i Filteri su zasad UI oznake načina u razvoju**, a ne aktivne obrade slike. Podrška za HDR/RAW/4K ovisi o uređaju i dolazi naknadno.

## 🎨 Vizualni identitet

<p align="center"><img src="branding/lensigo-icon.svg" alt="LENSIGO aperture icon" width="180"></p>

- **Graphite:** `#081827`
- **Midnight:** `#10364C`
- **Cyan:** `#00D4FF`
- **Blue:** `#2E83FF`
- **White:** `#F5FAFF`

## 📱 Referentni ekrani

Referentni dizajn sadrži 19 mobilnih i 19 tabletnih ekrana. Referentne PNG izvoze korisnik je dostavio zasebno; ovaj repozitorij sadrži SVG brending i implementaciju početnog zaslona s CameraX pregledom. **Nisu prikazane izmišljene snimke aplikacije.** Nakon stvarnih testova na uređajima ovdje će biti postavljeni stvarni screenshotovi.

## 🛠️ Razvoj i izgradnja

**Package**: `com.brendigo.lensigo` · **minSdk**: 24 · **compileSdk**: 36.

Build on a machine with Java 17, Android SDK 36 and Gradle 9.6:
```sh
gradle :app:assembleDebug
gradle :app:bundleRelease
```
APK: `app/build/outputs/apk/debug/app-debug.apk`. An unsigned release AAB is **not** Play-ready; production signing must be configured privately.

## 📦 Izdanja

GitHub Actions attempts a new **preview release** on each update to `main` **only after successful build**. Debug APKs are test builds and **not production-signed**. CI build failures prevent release. Production publishing requires signing keys, device tests and store review.

## 🛡️ Sigurnost

No internet permission, no analytics or trackers. The camera and optional microphone permissions are requested at runtime. Images and videos are written locally through Android MediaStore. On Android 7–9, additional storage permission is needed for public media writing, so that compatibility path remains to be completed and tested.

## 📄 License & credits

Copyright © 2026 Brendigo. MIT license. Camera features use AndroidX CameraX (Android Open Source / Apache 2.0 license obligations apply). See `LICENSE`. Original brand assets are Brendigo-branded. Site: https://brendigo.com.
