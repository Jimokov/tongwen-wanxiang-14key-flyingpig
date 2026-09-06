#!/usr/bin/env python3
"""Demonstrate the lossy primary-letter mapping in the legacy 14-key layout.

This is intentionally a small, dependency-free regression probe.  It reads the
real Trime behavior.yaml and checks that every physical two-letter key emits
only its first letter.  Such a layout cannot transmit an untouched Xiaohe code.
"""

from __future__ import annotations

import argparse
import re
from pathlib import Path


KEY_PAIRS = (
    "qw", "er", "ty", "ui", "op", "as", "df", "gh", "jk", "l",
    "zx", "cv", "bn", "m",
)
PROBE = "xi jp wf ti"


def load_key_sends(path: Path) -> dict[str, str]:
    text = path.read_text(encoding="utf-8")
    sends: dict[str, str] = {}
    for pair in KEY_PAIRS:
        block = re.search(
            rf"(?ms)^  14key{pair}:\s*\n(.*?)(?=^  \S.*?:\s*\n|\Z)",
            text,
        )
        if not block:
            raise ValueError(f"Could not find 14key{pair} in {path}")
        sent = re.search(r"^\s*send:\s*([^\s#]+)", block.group(1), re.M)
        if not sent:
            raise ValueError(f"14key{pair} has no send action")
        sends[pair] = sent.group(1).lower()
    return sends


def fold(code: str, sends: dict[str, str]) -> str:
    lookup = {letter: sends[pair] for pair in KEY_PAIRS for letter in pair}
    return "".join(lookup.get(char, char) for char in code.lower())


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--behavior", type=Path, required=True)
    args = parser.parse_args()
    sends = load_key_sends(args.behavior)
    actual = fold(PROBE, sends)

    print(f"raw Xiaohe code:      {PROBE}")
    print(f"legacy 14-key stream: {actual}")
    if actual == PROBE:
        print("PASS: raw code survives the layout")
        return 0
    print("FAIL: raw Xiaohe code is lossy; pinyin-based selection cannot work here.")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
