<p align="center"><img src="branding/lensigo-hero.svg" alt="LENSIGO — Pro Camera by Brendigo" width="100%"></p>
<h1 align="center">LENSIGO · PRO CAMERA</h1>
<p align="center"><strong>Svaki kadar. Tvoja priča.</strong><br>Every frame. Your story.<br><a href="https://brendigo.com">BY BRENDIGO</a></p>

<p align="center"><img alt="Android 7+" src="https://img.shields.io/badge/Android-7.0%2B-00C8F5?style=for-the-badge"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-CameraX-2E83FF?style=for-the-badge"><img alt="Privacy" src="https://img.shields.io/badge/Privacy-Offline-10364C?style=for-the-badge"><img alt="License" src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge"></p>

## 📷 Profesionalniji kadar — čak i na skromnijem telefonu

LENSIGO je Brendigo Android kamera s jednostavnim sučeljem, lokalnim efektima i postavkama optimiziranima za slabije uređaje. Bez računa, oglasa, telemetrije ili internetske dozvole.

### ✨ Stvarne mogućnosti u verziji 0.2.0

| 📸 Foto | 🎞️ Video | 🎨 Filteri | 💡 Poboljšanja |
|---|---|---|---|
| Fokus dodirom, omjer 4:3/16:9, zum, bljeskalica | Snimanje SD/HD prema dostupnosti uređaja; mikrofon samo uz dozvolu | 12 lokalnih efekata s regulacijom intenziteta | EV kompenzacija, posvjetljivanje noćnih snimaka, blago zaglađivanje šuma, izoštravanje |
| 📁 Galerija | 🪄 Uređivanje | 📱 Kompatibilnost | 🔐 Privatnost |
| Snimke kreirane aplikacijom | Svjetlina, kontrast, zasićenost, filtri i spremanje nove kopije | Android 7.0+, način ograničene memorije | MediaStore, lokalna obrada, bez cloud servisa |

**12 efekata:** Original · Vivid · Film · Mono · Warm · Cool · Vintage · Fade · Dramatic · Sepia · Noir · Sunset.

> Nema čarobnog povećanja stvarne rezolucije: filtri poboljšavaju izgled postojećih fotografija, ali ne mogu stvoriti detalje koje senzor nije snimio. Za Android 12+ predviđen je grafički pregled filtera na kameri; na starijim verzijama učinak se vidi na spremljenoj snimci i u editoru.

## 🎨 Originalni Brendigo identitet

<p align="center"><img src="branding/lensigo-icon.svg" alt="LENSIGO aperture" width="150"></p>

**Graphite** `#081827` · **Midnight** `#10364C` · **Cyan** `#00D4FF` · **Blue** `#2E83FF` · **White** `#F5FAFF`.

Svi zasloni koriste tamnu navigaciju, čitljive kontrole i lokalni prikaz kamere. Za 1:1 provjeru svake referentne slike: [detaljna tablica implementacije i statusa](docs/SCREEN-MATCHING.md).

Originalni korisnički referentni paket sadrži 19 mobilnih i 19 tabletnih konceptnih PNG-ova. Koncepti **nisu** predstavljeni kao stvarni screenshotovi aplikacije; stvarni se screenshotovi dodaju nakon testova.

## ⚙️ Tehnička arhitektura

- Kotlin + Jetpack Compose + AndroidX CameraX; MediaStore i Camera2 kompatibilne kontrole.
- Uređivanje u pozadini, bez uploadanja slika; JPEG obrada ograničena na 2048–3072 px zbog memorije.
- SD video postavljen kao prednost na slabijim uređajima; HD može biti nedostupan.
- Razdvojena jezgra: `FilterEngine.kt`, `MediaLibrary.kt`, `GalleryScreen.kt`, `MainActivity.kt`.
- **Package** `com.brendigo.lensigo`, minSdk 24, targetSdk 36.

```shell
gradle :app:assembleDebug
gradle :app:bundleRelease
```

GitHub Actions nakon **uspješnog** builda objavljuje novi preview Release s `app-debug.apk` (testni APK) i `app-release.aab` (nepotpisani bundle). Za produkcijsku objavu potreban je zaseban sigurni signing workflow i testiranje stvarnih uređaja.

### 🧭 Ostalo u planu

Portretno zamućenje, panorama, dual kamera, ručni ISO/shutter/focus, napredni višekadarski Night Mode i potpuni pixel-by-pixel audit 19 zaslona. Neaktivne funkcije nisu predstavljene kao gotove.

### 🔒 Privatnost i licenca

© 2026 Brendigo — https://brendigo.com · MIT LICENSE. CameraX i druge AndroidX komponente imaju vlastite open-source licencne obveze.
