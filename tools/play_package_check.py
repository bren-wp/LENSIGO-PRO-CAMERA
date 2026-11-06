#!/usr/bin/env python3
"""Fail closed if an APK/AAB contains unverified native ELF .so files."""
import sys
import zipfile
from pathlib import Path

def verify(path: Path) -> None:
    if path.suffix not in (".apk", ".aab"):
        raise ValueError("Only APK/AAB are accepted")
    with zipfile.ZipFile(path) as archive:
        names = archive.namelist()
        bad = [name for name in names if name.endswith(".so")]
        if bad:
            raise RuntimeError("Native libraries require 16 KB alignment checks; "
                "verification not yet implemented: " + ", ".join(bad))
        if path.suffix == ".apk" and "AndroidManifest.xml" not in names:
            raise RuntimeError("APK is missing its manifest")
        if path.suffix == ".aab" and "base/manifest/AndroidManifest.xml" not in names:
            raise RuntimeError("AAB is missing base manifest")
        print(f"{path}: manifest found; no native .so binaries; package preflight passed.")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        raise SystemExit("Usage: play_package_check.py file.apk file.aab")
    for candidate in sys.argv[1:]:
        verify(Path(candidate))
