# LENSIGO — zasloni, referentne dimenzije i status

Izvor: korisnikov paket `LENSIGO_Branding_Android_UI_19_ekrana(1).zip`.

**Referentni mobilni format:** 1080 × 2400 px. **Tablet:** 1600 × 2560 px.
Rekonstrukcija se izrađuje u Jetpack Compose; screenshotovi koncepta nisu stvarne snimke Android aplikacije. Za potvrdu *pixel-perfect* potrebni su screenshot testovi na ciljanim rezolucijama i usporedba boja i rubova. Još nema potvrđene 1:1 podudarnosti.

| Referenca | Zaslon | Implementacija | Status |
|---|---|---|---|
| 01_splash.png | Splash | Brendirani Compose Splash i simbol LENSIGO | Funkcionalno, nije vizualno 1:1 potvrđeno |
| 02_onboarding.png | Dobrodošlica | Brendirani prikaz i gumb Započni | Funkcionalno, nije 1:1 potvrđeno |
| 03_permissions.png | Dozvole | Runtime kamera/pohrana/mikrofon | Funkcionalno, nije vizualno 1:1 |
| 04_home.png | Početni izbornik | Responzivne kartice: 8 CameraX načina rada, galerija, postavke, o aplikaciji | Implementirano; 1:1 nije mjereno |
| 05_photo.png | Foto | Preview, 4:3/16:9, bljeskalica, fokus, zoom, okidač | Funkcionalno, 1:1 nije potvrđeno |
| 06_video.png | Video | HD/SD, opcionalni zvuk, recording | Funkcionalno, 1:1 nije potvrđeno |
| 07_pro.png | Pro | ISO/S/EV/WB/MF panel, Camera2 manual kontrola uz otkrivanje podrške, zum | Funkcionalni hardverski uvjetovani klizači; 1:1 nije potvrđeno |
| 08_night.png | Noć | Proizvođački Night preko CameraX Extensions, EV + digitalni fallback | Funkcionalno uz ograničenja; 1:1 nije potvrđeno |
| 09_portrait.png | Portret | Proizvođački Bokeh ili lokalno zamućenje središnje elipse, klizač | Funkcionalno uz ograničenja; 1:1 nije potvrđeno |
| 10_panorama.png | Panorama | 2–5 vođenih snimki, indikator pomaka, završetak/odustajanje, lokalno spajanje | Osnovni način bez automatskog poravnanja; 1:1 nije potvrđeno |
| 11_dual.png | Dual | Istodobni pregled prednje/stražnje kamere uz podršku, sekvencijalni fallback, spajanje fotografija | Osnovni način uz hardverska ograničenja; 1:1 nije potvrđeno |
| 12_filters.png | Filteri | 12 lokalnih LUT/matrix efekata i intenzitet | Funkcionalno za JPEG; preview podrška Android 12+ |
| 13_gallery.png | Galerija | 3 stupca, 4 filtra (Sve/Fotografije/Videozapisi/Favoriti), oznake na snimkama | Implementirano, 1:1 nije potvrđeno |
| 14_viewer.png | Preglednik | Foto/video, Uredi, Podijeli, Spremi kopiju, Izbriši, favorit | Implementirano, 1:1 nije potvrđeno |
| 15_editor.png | Uređivač | Zajednička preview/export obrada crop/rotate/filter, 12 efekata, max 840 px u pretpregledu | Funkcionalno unutar implementiranog opsega, 1:1 nije potvrđeno |
| 16_settings.png | Postavke | JPEG, lokalna obrada, SD video, mreža, timer i opcionalni zvuk | Implementirano, 1:1 nije potvrđeno |
| 17_camera-settings.png | Kamera postavke | Zaseban ekran JPEG, mreža i video kvaliteta | Implementirano djelomično, 1:1 nije potvrđeno |
| 18_privacy.png | Privatnost | Brendirana četiri retka, simbol, informativni panel i otvaranje Android dozvola | Implementirano; 1:1 nije potvrđeno |
| 19_about.png | O aplikaciji | Simbol i wordmark, naziv, web-stranica, licence, privatnost i stvarna verzija | Implementirano; 1:1 nije potvrđeno |

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


## Pro / Night / Portrait — vizualna provjera 0.3.0

Iz `izvoz/mobitel/07_pro.png`, `08_night.png` i `09_portrait.png`:
- Referentni status bar i header su tamni, s bijelim oznakama i cijan aktivnim stanjem.
- PRO: 5 kontrola ISO / S / EV / WB / MF ispod pregleda, dok pri dnu ostaju zoom, način rada i bijeli okidač.
- NOĆ: pregled zauzima većinu visine, zoom je uz donji rub fotografije, odabrani NOĆ je cijan.
- PORTRET: vodoravni klizač ZAMUĆENJE preko fotografije, bez glumljenja dubinske maske na uređajima bez Bokeh dodatka.
- Za manje ekrane koriste se Compose dp i proporcionalno raspoređeni elementi; nije obećana 1:1 usporedba dok nisu prikupljeni stvarni screenshotovi na usporedivim uređajima.

**Hardverske funkcije:** RAW nije implementiran; Pro zaslon prikazuje JPEG, a ISO/S/MF se deaktiviraju na uređajima bez ručne Camera2 kontrole. Proizvođačke Night/Bokeh ekstenzije aktiviraju se samo nakon `isExtensionAvailable`. Kad ekstenzija izostane, lokalni Noćni način i portretna eliptična maska ostaju dostupni. Lokalni portret ograničava izlaz na 1600 px da se smanji memorijsko opterećenje.

**Obvezna provjera prije 1:1 oznake:** screenshot tests na 1080×2400 i 1600×2560, uz toleranciju za statusnu/navigacijsku traku, font scaling, kutove, boje, klizače, fiksne visine i stvarni OEM prikaz kamere.

## Panorama / Dual i pokretanje — pregled 0.4.0

- Referenca 10: tamna traka na vrhu, živa kamera, bijeli okvir s porukom za pomicanje udesno i napredak, odabrana PANORAMA oznaka u cijanu, bijeli okidač, povratak u galeriju.
- Referenca 11: velika stražnja kamera i mali portretni prednji pregled (kad je dostupan), tamni donji izbornik, cijan DUAL, bijeli okidač. Na uređajima bez concurrent podrške u malom okviru piše zašto nema prikaza, a zatim se kamera prebacuje za drugu snimku.
- Reference 01–03: namjenski Splash, dobrodošlica i ekrani dozvola. SVG/vector simbol približna je vektorska rekonstrukcija, nije dokazano pixel 1:1 s dostavljenim PNG-om.
- **Nije odrađeno:** snimanje stvarnih screenshotova na Android emulatoru/fizičkom uređaju, diffs/threshold s PNG referencama, simultana ekspozicija oba senzora, perspektivna registracija panorame.
- Responzivnost: klizači i gumbi koriste Compose dp te prilagodljivu veličinu pregleda kamere, ali rasporedi trebaju testove na malim mobitelima, tabletima i velikom fontu.

## Galerija, preglednik, editor i postavke 0.5.0

Referentne slikovne datoteke `13_gallery.png` do `17_camera-settings.png` imaju:
- gornju tamnoplavu traku sa strelicom i centriranim naslovom
- podebljane naslovne blokove s plavim naglascima
- galeriju s tri stupca i četiri vrste sadržaja
- preglednik s donjom trakom od četiri akcije
- editor s velikom fotografijom, pet alata, jednim klizačem i gumbom za spremanje
- postavke s vizualnim grupama; implementirane su samo stavke koje zaista mijenjaju ponašanje kamere

U ovom izdanju postoji odgovarajuća navigacija i funkcionalni alati, ali **još nema potvrde 1:1**. Stvarna kamera i galerija s korisničkim slikama ne mogu se automatski smatrati istom statičnom scenom kao referentni mockup. Potrebne su realne snimke Android emulatora/uređaja za usporedbu koordinata, fontova, paddinga i boja.

## Originalni 512px launch icon — 0.5.x

Ponovno je upotrijebljen originalni Python/CairoSVG generator iz referentnog Brendigo paketa. Izlaz ima identičan RGB sadržaj kao `01_ikona_512x512.png` (512×512, SHA-256 rasterskih piksela `96538ced13ae75393f3feed7a06866a5d9f72f59e35d359c10dc67cca958e2df`). GitHub Action provjerava identitet prije pakiranja. To **ne potvrđuje** vidljivu 1:1 ikonu na svim Android launcherima jer sustav može promijeniti masku, skaliranje i rubove.

## 18 Privatnost / 19 O aplikaciji — implementacija 0.6.0

Uspoređene su korisnikove mobilne reference 1080 × 2400. Dodani su odgovarajući tamnoplavi layout, bijela tipografija, cijan simbol objektiva i grupirane informacije. Na zaslonu privatnosti četiri su reda i informativni panel; na zaslonu O aplikaciji četiri su informacije, aktivni navigacijski redovi i oznaka Brendigo. Za razliku od statičnih mockupova, poveznice otvaraju stvarne stranice ili Android dozvole. Dodana je zasebna stranica licenci.

UI je responzivan kroz Compose `widthIn(max=540.dp)`, okomito pomicanje i `heightIn`, no **vizualno 1:1 nije potvrđeno** bez stvarnih snimki aplikacije na telefonu i tabletu. Izvorna 512×512 launcher slika reproducira se RGB-piksel-po-piksel; Android maskiranje može promijeniti prikaz ikone na početnom zaslonu.

## Nastavak nakon v0.7.21 — Splash i dozvole

- **01 Splash:** standardni raspored i izvorni LENSIGO simbol ostaju nepromijenjeni, a uski i niski prozori dobivaju kompaktniji raspored koji se može pomicati umjesto odrezivanja podnožja.
- **03 Dozvole:** zahtjevi za kameru i pohranu sada su zasebne akcije. Na Androidu 10+ vlastite snimke ne traže zastarjelu dozvolu za pohranu; na Androidu 7–9 ostaje zaseban zahtjev.
- **Izdavanja:** GitHub CI broj izdanja sada je usklađen s Android `versionName` i rastućim `versionCode`, umjesto da svi binarni paketi zadržavaju 0.7.0.
- **Nije potvrđeno:** 1:1 usporedba sa stvarnim 1080×2400 i 1600×2560 Android snimkama, velike pristupačne veličine fonta, ponašanje CameraX na fizičkim uređajima i potpisani Play AAB.

## Nastavak — zasloni 02/03 i pristupačnost

- **02 Dobrodošlica:** na standardnim zaslonima očuvan je postojeći raspored i tipografija. Na niskim/uskim prozorima koristi se manji okvir logotipa i okomito pomicanje radi dostupnog gumba Započni i većeg teksta.
- **03 Dozvole:** mala visina i široko povećanje fonta više ne ovise o elementu koji raste unutar nedovoljno visokog zaslona; sadržaj se pomiče, tako da korisnik može doći do gumba Nastavi.
- **Vizualni status:** nema mjerenja stvarnih snimaka uređaja za 02/03, stoga nije potvrđena 1:1 podudarnost s referentnim PNG-ovima.

## Nastavak — zaslon 04 Početna stranica

- Standardna mobilna referentna mreža od dva stupca i tabletna od četiri ostaje nepromijenjena.
- Na širini manjoj od 340 dp prelazi se na jedan stupac da nazivi načina snimanja ne budu odrezani.
- Kartice dopuštaju dva retka na velikom fontu; zaglavlje i banner prilagođavaju se većim fontovima, a akcije s ikonama imaju pristupačnije oznake.
- **Preostaje:** stvarna usporedba geometrije i boja s 04_home.png, test Android čitača zaslona i instrumentirano testiranje na uređaju.

## Splash reference reconstruction after v0.7.24

From the original `LENSIGO_Branding_Android_UI_19_ekrana(1).zip`:
- Phone source: `izvoz/mobitel/01_splash.png` (1080×2400); tablet: `izvoz/tablet/01_splash.png` (1600×2560).
- The supplied design has a dark `#0E2134` upper background fading to `#08111E`, an aperture starting around 24.5% from the top, four lower cyan/navy arcs and a cyan circular loading indicator.
- This revision models the arcs with Compose Canvas, positions the logo proportionally and hides Android system bars only while the Splash is visible. Regular Android navigation and status bars are restored on later screens.
- **Verification pending:** the above is a source-guided layout reconstruction, NOT an emulator screenshot comparison. OEM system bar transitions, density/font scale and exact pixel scores still need device capture.

## Home reference photography + icon fidelity (original package)

- The source `assets/photo.jpg` (same `LENSIGO_Branding_Android_UI_19_ekrana(1).zip`) supplied the hero artwork for mobile reference 04 and its camera scene. This revision packages a local, center-cropped **300×134 WebP** (about 3 KB) as `res/drawable-nodpi/lensigo_home_hero.webp`. It is not a fabricated landscape or remote image.
- Eight camera-mode icons have changed from filled Material glyphs to outlined variants closer to the 04 reference. Standard card layout (two columns on phone, four on tablet, single narrow-phone fallback) is preserved.
- The smaller WebP is lossy; this is a UI fidelity improvement, not a proof of exact original image pixels. Improve the asset resolution when the full source can be safely checked in, and compare real Android emulator screenshots against 04_home.png before marking 1:1.

## Photo 05 — focus frame and actual thumbnail

- The 78dp preview focus reticle starts centred and follows the real user tap, clamped inside the live CameraX preview while the same tap is sent for metering.
- The bottom-left gallery shortcut displays a bounded, locally decoded last-photograph thumbnail after capture. The image decoding uses Dispatchers.IO; without saved media the shortcut remains an icon, not a mockup.
- The shutter has contextual screen-reader actions for still photo or starting/stopping video.
- Pending: device screenshots and actual OEM autofocus/lens testing. Photo-mode HDR has not been implemented merely to match static reference text.

## References 02 Welcome / 03 Permissions — visual and functional alignment

- Source: `izvoz/mobitel/02_onboarding.png`, `03_permissions.png` and corresponding 1600×2560 tablet PNG files. Standard Welcome cards retain the navy panel, but the aperture is reduced to approximately the reference size (112dp) and page indicators use real filled/outlined circles.
- Welcome/Permissions now show functional back and system settings actions: first-screen back exits, the Permissions back returns to Welcome, and settings opens Android's real app settings rather than an inaccessible or mock dialog.
- The Permissions layout now follows the reference's camera/microphone/media/location sequence. Location is explicitly **not collected or written to EXIF**, rather than promising a permission-dependent feature that the Android app has not implemented. Android 10+ still does not request legacy storage permission.
- **Still unverified:** real screenshots / pixel-diff, large font layout and Android 7–9 permission round-trip. This is reference-driven implementation, not confirmed 1:1.

## 06 Video Camera — recording overlay and ratio

- CameraX `VideoRecordEvent.Status.recordingStats.recordedDurationNanos` now drives the visible red recording timer, which shows hours/minutes/seconds. It appears only during actual recording, never as a static fake 00:00:12 label from the source screenshot.
- First video session starts in 16:9 as reference 06, while still-photo modes start in 4:3. The user can change each ratio independently and both preferences persist locally.
- Source `izvoz/mobitel/06_video.png` and tablet equivalent. The mockup's 4K/60 label is not displayed because only SD/HD capture has been implemented/tested. Device-specific resolution, lens switching while idle, microphone and preview geometry require real physical tests.

## 07 Pro Camera — reference-guided manual control panel

- The supplied `07_pro.png` has five lower dark cards (ISO, S, EV, WB, MF) and a white graduated ruler below. The native Compose overlay now uses 48dp minimum target height for manual controls and renders a 40-tick ruler, with a **cyan indicator linked to the actual selected manual value** (not a decorative fake needle).
- Only genuine Camera2 manual-capability ranges are shown as usable. If the device does not support a control, that control remains disabled and gives honest feedback. The design mockup uses “RAW” and Kelvin values; the actual app currently captures JPEG and exposes available AWB modes, so neither RAW nor a Kelvin slider is falsely advertised.
- Still needs emulator/physical screenshots and pixel-audit comparison on phone/tablet, accessibility reader and hardware capability checks. Added ruler is a fidelity improvement, not proof of a perfect 1:1 match.

## 08 Night — source-driven uncluttered preview

- The original `izvoz/mobitel/08_night.png` and tablet counterpart show the live image without an extra persistent footer label, and a standard zoom control just above the selected mode. The hardcoded footer text has been removed to restore usable preview height.
- The existing real exposure compensation slider remains available on devices supporting EV, but is **collapsed by default** and toggled by tapping the accessible NOĆ header. When the OEM Night extension is active or EV unsupported, no non-functional slider is presented. The actual vendor-extension/local-processing decision remains unchanged.
- Camera sensor content changes on each device; no claim is made that a live preview matches the static reference photograph. Device screenshots and extension fallback tests remain pending.

## 09 Portrait — honest fallback and visual focus guide

- The user reference `izvoz/mobitel/09_portrait.png` includes a large face-area focus frame and a cyan blur slider. Native Portrait mode uses a 210dp focus guide, still responds to actual tap-to-focus and clamps inside the real CameraX preview.
- When CameraX Bokeh extension is available, top header reads BOKEH; when it is not, top header reads PORTRET and the local oval-mask blur slider remains functional. No imaginary device-level background segmentation is claimed, and the extra developer-like footer note has been removed from the view.
- Native screenshot comparison against the 1080×2400 phone and 1600×2560 tablet reference is not yet recorded. Face-specific tracking is not implemented; the rectangle is a visible focus guide, not a face detector.

## 10 Panorama / 11 Dual — framing and hardware capability

- Source `izvoz/mobitel/10_panorama.png`: white translucent central guidance rectangle with the rightward movement instruction, a wide horizontal indicator and a large white arrow. The guidance now follows these elements; the line fill reflects `frameCount / MAX_FRAMES`, not an invented 100% sample progress. Existing Finish/Cancel and 2–5 frame logic remain functional.
- Source `izvoz/mobitel/11_dual.png`: front-view PiP in the upper-right of the main camera image. CameraX's actual concurrent view remains visible only when hardware supports it. Removed redundant footer text during concurrent operation; numbered guidance remains visible for the honest sequential fallback.
- Not verified: panorama perspective alignment, simultaneous still exposure across dual sensors, OEM CameraX camera behavior, or exact 1:1 Android screenshots. Both modes retain existing documented limitations.

## 12 Filters — genuine, source-backed thumbnail strip

- The reference 12_filters.png shows small photographic previews across the bottom of the camera. The old text-only chips are replaced with locally produced photographic previews, using the real source-provided `assets/photo.jpg` optimized for the Home asset. Each preview passes through the **same FilterEngine** and selected intensity as final JPEG processing, avoiding misleading arbitrary color swatches.
- All 12 existing filters remain navigable via a horizontally scrolling strip. The chosen filter is outlined in cyan; previews render off the UI thread with a debounce to limit work while the user drags intensity. No external images, network permissions or cloud processing are introduced.
- Remaining: exact image, font and spacing comparison with original mobile/tablet PNG; actual CameraX preview transformation and low-RAM/long-session profiling are not yet device-verified.

## 13 Gallery / 14 Viewer / 15 Editor navigation

- Gallery keeps three columns of actual LENSIGO MediaStore thumbnails; on narrow widths under 340dp the four category pills become a horizontally scrollable strip instead of truncating names. Other widths retain the reference four-pill row. No developer mockup photographs are inserted in users' galleries.
- Viewer top-right now uses the original reference's horizontal ellipsis menu; Favorite toggle remains functional inside it, alongside Share, Save copy and Delete. Bottom action bar remains accessible.
- Editor's top-right gear now opens actual app settings. Image transformations still use the shared crop/rotate/filter pipeline and save a separate copy rather than overwriting originals.
- Emulator/physical-screen pixel comparisons for all three screens are not yet recorded; real photographs differ from source-designed scene samples.

## 16 Settings / 17 Camera Settings — source-aligned, real controls only

- Reference Settings 16 contains Kamera, Privatnost i pohrana, Aplikacija panels. The real Android menu preserves this main hierarchy. Advanced noise/sharpen/low-memory controls remain **in the actual Camera Settings 17 screen**, rather than appearing in a duplicated extra panel on the main Settings page. The microphone permission remains available in Camera Settings.
- Reference Camera Settings 17 includes an aspect ratio. Added a persisted real 4:3/16:9 still-photo preference, synced with the camera header and retained when switching between Photo and Video. JPEG quality, grid, SD/HD preference and all pre-existing real processing controls still operate.
- The mockup's RAW/HEIF/4K60/HDR toggles were not inserted because the current CameraX capture pipeline has no verified implementation for them. Device-level screenshots and compatibility testing remain required.
