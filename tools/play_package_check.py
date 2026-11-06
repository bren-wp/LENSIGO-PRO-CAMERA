#!/usr/bin/env python3
"""Google Play native-library 16 KiB preflight for APK/AAB.

Inspects *actual* ELF PT_LOAD segments for 64-bit ABIs, rather than banning
every packaged .so. For APKs, also checks the local ZIP payload offset when
libraries are stored without compression. Bundletool output must additionally
be verified in Play Console (this script does not sign releases).
"""
import argparse
import struct
import sys
import zipfile
from pathlib import Path

PAGE_SIZE = 16 * 1024
ABIS_64 = ("arm64-v8a", "x86_64", "riscv64")


def _integer(data: bytes, offset: int, size: int, endian: str) -> int:
    if offset + size > len(data):
        raise ValueError("Truncated ELF")
    return int.from_bytes(data[offset:offset + size], endian)


def elf_load_segments(content: bytes) -> list[tuple[int, int, int]]:
    if content[:4] != b"\x7fELF":
        raise ValueError("Native file is not ELF")
    cls = content[4]
    if cls not in (1, 2) or content[5] not in (1, 2):
        raise ValueError("Unsupported ELF architecture/endianness")
    endian = "little" if content[5] == 1 else "big"
    is_64 = cls == 2
    phoff = _integer(content, 32 if is_64 else 28, 8 if is_64 else 4, endian)
    entsize = _integer(content, 54 if is_64 else 42, 2, endian)
    count = _integer(content, 56 if is_64 else 44, 2, endian)
    if count > 1024 or (entsize < (56 if is_64 else 32)):
        raise ValueError("Invalid ELF program-header table")
    found = []
    for index in range(count):
        offset = phoff + index * entsize
        if offset + entsize > len(content):
            raise ValueError("Truncated ELF program headers")
        typ = _integer(content, offset, 4, endian)
        if typ != 1:
            continue
        poff = _integer(content, offset + 8 if is_64 else offset + 4,
                        8 if is_64 else 4, endian)
        vaddr = _integer(content, offset + 16 if is_64 else offset + 8,
                         8 if is_64 else 4, endian)
        align = _integer(content, offset + 48 if is_64 else offset + 28,
                         8 if is_64 else 4, endian)
        found.append((poff, vaddr, align))
    if not found:
        raise ValueError("ELF has no loadable segments")
    return found


def _zip_data_offset(stream, info: zipfile.ZipInfo) -> int:
    stream.seek(info.header_offset)
    header = stream.read(30)
    if len(header) != 30 or header[:4] != b"PK\x03\x04":
        raise ValueError("Invalid ZIP local header")
    filename_size, extra_size = struct.unpack_from("<HH", header, 26)
    return info.header_offset + 30 + filename_size + extra_size


def verify(path: Path) -> None:
    if path.suffix not in (".apk", ".aab"):
        raise ValueError("Only APK/AAB are accepted")
    issues = []
    libs = 0
    with zipfile.ZipFile(path) as archive, path.open("rb") as raw:
        names = archive.namelist()
        expected_manifest = ("AndroidManifest.xml" if path.suffix == ".apk" else
                             "base/manifest/AndroidManifest.xml")
        if expected_manifest not in names:
            issues.append("Missing Android manifest: " + expected_manifest)
        for entry in archive.infolist():
            if not entry.filename.endswith(".so"):
                continue
            libs += 1
            parts = entry.filename.split("/")
            abi = parts[-2] if len(parts) >= 2 else ""
            if abi not in ABIS_64:
                print("  32-bit/native ABI (16 KB not required here):", entry.filename)
                continue
            try:
                segments = elf_load_segments(archive.read(entry))
                for off, va, align in segments:
                    if align < PAGE_SIZE or off % PAGE_SIZE != va % PAGE_SIZE:
                        issues.append(
                            f"{entry.filename}: PT_LOAD p_align={align}, offset={off}, "
                            f"vaddr={va}; requires 16 KiB-compatible ELF"
                        )
                if path.suffix == ".apk" and entry.compress_type == zipfile.ZIP_STORED:
                    data_offset = _zip_data_offset(raw, entry)
                    if data_offset % PAGE_SIZE:
                        issues.append(
                            f"{entry.filename}: stored ZIP payload offset {data_offset} "
                            f"is not 16 KiB aligned"
                        )
                print(f"  {entry.filename}: ELF PT_LOAD alignment checked")
            except (ValueError, OSError, struct.error) as exc:
                issues.append(f"{entry.filename}: {exc}")
    if issues:
        raise RuntimeError("Google Play 16 KiB preflight FAILED:\n - " + "\n - ".join(issues))
    print(f"{path}: manifest found, {libs} native libraries inspected, "
          "64-bit ELF/ZIP checks passed.")
    print("Note: Play Console and bundletool checks of the signed release are still required.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("packages", type=Path, nargs="+")
    args = parser.parse_args()
    try:
        for package in args.packages:
            verify(package)
    except (RuntimeError, ValueError, zipfile.BadZipFile) as error:
        print(str(error), file=sys.stderr)
        sys.exit(1)
