#!/usr/bin/env python3
"""Static audit of hardcoded user-facing Android text.

This intentionally scans all quoted Kotlin literals (not comments) as a broad
guard against placeholder/developer wording shipping to consumers. Legitimate
technical SDK names in code identifiers are not user-facing messages.
"""
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
APP = ROOT / "app" / "src" / "main"
FORBIDDEN = re.compile(
    r"(?i)\b(?:debug|demo|placeholder|lorem\s+ipsum|mockup|preview\s+build|"
    r"testni\s+build|razvojna\s+verzija|koncept\s+dizajna|"
    r"nije\s+implementiran(?:o|a)?|uskoro\s+dostupno|"
    r"privremeni\s+tekst|development\s+only)\b"
)
LITERAL = re.compile(r'"(?:\\.|[^"\\])*"', re.MULTILINE)

def audit() -> list[str]:
    errors = []
    for path in sorted(APP.rglob("*.kt")):
        data = path.read_text(encoding="utf-8")
        for match in LITERAL.finditer(data):
            quoted = match.group()
            if FORBIDDEN.search(quoted):
                line = data.count("\n", 0, match.start()) + 1
                errors.append(f"{path.relative_to(ROOT)}:{line}: prohibited UI copy {quoted}")
    for path in sorted((APP / "res").rglob("*.xml")):
        if path.name.lower().startswith("test"):
            continue
        data = path.read_text(encoding="utf-8")
        if path.parent.name == "values":
            for match in re.finditer(r">([^<]+)<", data):
                if FORBIDDEN.search(match.group(1)):
                    errors.append(f"{path.relative_to(ROOT)}: prohibited resource copy")
    return errors

if __name__ == "__main__":
    findings = audit()
    for problem in findings:
        print(problem)
    if findings:
        sys.exit(1)
    print("LENSIGO consumer UI copy audit: passed; no placeholder/developer wording.")
