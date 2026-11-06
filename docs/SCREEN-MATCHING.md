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
