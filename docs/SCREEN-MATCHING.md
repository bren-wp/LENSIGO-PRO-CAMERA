# LENSIGO — zasloni, referentne dimenzije i status

Izvor: korisnikov paket `LENSIGO_Branding_Android_UI_19_ekrana(1).zip`.

**Referentni mobilni format:** 1080 × 2400 px. **Tablet:** 1600 × 2560 px.
Rekonstrukcija se izrađuje u Jetpack Compose; screenshotovi koncepta nisu stvarne snimke Android aplikacije. Za potvrdu *pixel-perfect* potrebni su screenshot testovi na ciljanim rezolucijama i usporedba boja i rubova. Još nema potvrđene 1:1 podudarnosti.

| Referenca | Zaslon | Implementacija | Status |
|---|---|---|---|
| 01_splash.png | Splash | Brendirani Compose Splash i simbol LENSIGO | Funkcionalno, nije vizualno 1:1 potvrđeno |
| 02_onboarding.png | Dobrodošlica | Brendirani prikaz i gumb Započni | Funkcionalno, nije 1:1 potvrđeno |
| 03_permissions.png | Dozvole | Runtime kamera/pohrana/mikrofon | Funkcionalno, nije vizualno 1:1 |
| 04_home.png | Početni izbornik | Lokalni izbor načina rada | Funkcionalno, nije 1:1 |
| 05_photo.png | Foto | Preview, 4:3/16:9, bljeskalica, fokus, zoom, okidač | Funkcionalno, 1:1 nije potvrđeno |
| 06_video.png | Video | HD/SD, opcionalni zvuk, recording | Funkcionalno, 1:1 nije potvrđeno |
| 07_pro.png | Pro | ISO/S/EV/WB/MF panel, Camera2 manual kontrola uz otkrivanje podrške, zum | Funkcionalni hardverski uvjetovani klizači; 1:1 nije potvrđeno |
| 08_night.png | Noć | Proizvođački Night preko CameraX Extensions, EV + digitalni fallback | Funkcionalno uz ograničenja; 1:1 nije potvrđeno |
| 09_portrait.png | Portret | Proizvođački Bokeh ili lokalno zamućenje središnje elipse, klizač | Funkcionalno uz ograničenja; 1:1 nije potvrđeno |
| 10_panorama.png | Panorama | 2–5 vođenih snimki, indikator pomaka, završetak/odustajanje, lokalno spajanje | Osnovni način bez automatskog poravnanja; 1:1 nije potvrđeno |
| 11_dual.png | Dual | Istodobni pregled prednje/stražnje kamere uz podršku, sekvencijalni fallback, spajanje fotografija | Osnovni način uz hardverska ograničenja; 1:1 nije potvrđeno |
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
