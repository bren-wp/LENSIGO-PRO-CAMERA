import struct
import tempfile
import unittest
import zipfile
from pathlib import Path
from play_package_check import elf_load_segments, verify

def make_elf64(alignment: int) -> bytes:
    content = bytearray(128)
    content[:4] = b"\x7fELF"
    content[4] = 2
    content[5] = 1
    struct.pack_into("<Q", content, 32, 64)
    struct.pack_into("<H", content, 54, 56)
    struct.pack_into("<H", content, 56, 1)
    struct.pack_into("<IIQQQQQQ", content, 64,
        1, 5, 0, 0, 0, 128, 128, alignment)
    return bytes(content)

class PlayPackageCheckTest(unittest.TestCase):
    def test_large_page_elf(self):
        self.assertEqual(elf_load_segments(make_elf64(16384)), [(0,0,16384)])

    def test_reject_small_page_elf(self):
        with tempfile.TemporaryDirectory() as folder:
            package = Path(folder) / "bad.aab"
            with zipfile.ZipFile(package, "w") as z:
                z.writestr("base/manifest/AndroidManifest.xml", b"test")
                z.writestr("base/lib/arm64-v8a/libexample.so", make_elf64(4096))
            with self.assertRaisesRegex(RuntimeError, "16 KiB"):
                verify(package)

    def test_accept_16k_elf_in_bundle(self):
        with tempfile.TemporaryDirectory() as folder:
            package = Path(folder) / "good.aab"
            with zipfile.ZipFile(package, "w") as z:
                z.writestr("base/manifest/AndroidManifest.xml", b"test")
                z.writestr("base/lib/arm64-v8a/libexample.so", make_elf64(16384))
            verify(package)

    def test_manifest_required(self):
        with tempfile.TemporaryDirectory() as folder:
            package = Path(folder) / "bad.apk"
            with zipfile.ZipFile(package, "w") as z:
                z.writestr("assets/x", b"x")
            with self.assertRaisesRegex(RuntimeError, "manifest"):
                verify(package)

if __name__ == "__main__":
    unittest.main()
