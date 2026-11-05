# LENSIGO — zasloni, referentne dimenzije i status

Izvor: korisnikov paket `LENSIGO_Branding_Android_UI_19_ekrana(1).zip`.

**Referentni mobilni format:** 1080 × 2400 px. **Tablet:** 1600 × 2560 px.
Rekonstrukcija se izrađuje u Jetpack Compose; screenshotovi koncepta nisu stvarne snimke Android aplikacije. Za potvrdu *pixel-perfect* potrebni su screenshot testovi na ciljanim rezolucijama i usporedba boja i rubova. Još nema potvrđene 1:1 podudarnosti.

| Referenca | Zaslon | Implementacija | Status |
|---|---|---|---|
| 01_splash.png | Splash | Launcher ikona / standardno pokretanje | Djelomično |
| 02_onboarding.png | Dobrodošlica | Planirano | Nije dovršeno |
| 03_permissions.png | Dozvole | Runtime kamera/pohrana/mikrofon | Funkcionalno, nije vizualno 1:1 |
| 04_home.png | Početni izbornik | Lokalni izbor načina rada | Funkcionalno, nije 1:1 |
| 05_photo.png | Foto | Preview, 4:3/16:9, bljeskalica, fokus, zoom, okidač | Funkcionalno, 1:1 nije potvrđeno |
| 06_video.png | Video | HD/SD, opcionalni zvuk, recording | Funkcionalno, 1:1 nije potvrđeno |
| 07_pro.png | Pro | EV slider + zum + kvalitetniji JPEG | Djelomično; ISO/shutter/focus nisu ručni |
| 08_night.png | Noć | EV i lokalno posvjetljivanje JPEG-a | Djelomično; nema višekadarskog night stackinga |
| 09_portrait.png | Portret | Planirano | Nije implementirano |
| 10_panorama.png | Panorama | Planirano | Nije implementirano |
| 11_dual.png | Dual | Planirano, ovisno o hardveru | Nije implementirano |
| 12_filters.png | Filteri | 12 lokalnih LUT/matrix efekata i intenzitet | Funkcionalno za JPEG; preview podrška Android 12+ |
| 13_gallery.png | Galerija | Lokalno pretraživanje i prikaz LENSIGO datoteka | Funkcionalno |
| 14_viewer.png | Preglednik | Fotografije i vanjski video player | Funkcionalno |
| 15_editor.png | Uređivač | Filter, kontrast, svjetlina, zasićenost, spremanje kopije | Funkcionalno |
| 16_settings.png | Postavke | Kvaliteta, obrada, šum, SD video, mikrofon | Funkcionalno |
| 17_camera-settings.png | Kamera postavke | U glavnim postavkama | Djelomično |
| 18_privacy.png | Privatnost | Lokalni opis dozvola | Funkcionalno |
| 19_about.png | O aplikaciji | Brending i verzija | Funkcionalno |

## Redoslijed dorade

1. Razvojni build i provjera na Android emulatoru + fizičkom telefonu.
2. Foto i Filteri: tipografija, geometrija, stvarni screenshoti, usporedba tolerancije.
3. Video / Galerija / Viewer / Editor.
4. Pro ručne Camera2 kontrole gdje hardver dopušta; softverski fallback.
5. Noćni kompozit, panorama, portretno segmentiranje i dual kamera uz provjeru sposobnosti.
6. Dovršetak Splash/Onboarding, HR/EN lokalizacije i tablet layouta.
7. Secure release signing i produkcijsko testiranje.

## Pošteno označavanje poboljšanja slike

Filtri mogu podesiti kontrast, boje, svjetlinu i lokalno izoštravanje, ali ne mogu vratiti detalje koje senzor nije zabilježio. `lowMemory` ograničava dimenziju slike na 2048 px pri obradi; bez filtra originalna JPEG datoteka sprema se bez recompressiona. `softenNoise` koristi blago lokalno zaglađivanje (nije ML rekonstrukcija).
