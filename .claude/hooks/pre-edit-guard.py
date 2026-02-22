#!/usr/bin/env python3
"""
NEBULA FACTORY — Zone 1: Pre-Edit Quality Gate
Runs before every Edit/Write tool call.
Warns on sensitive file modifications before Claude proceeds.
Input: JSON from stdin with tool call data.
Exit 0 = allow. Exit 2 = block with message.
"""
import sys
import json

try:
    data = json.load(sys.stdin)
except (json.JSONDecodeError, Exception):
    sys.exit(0)

fp = data.get("file_path", "")
warnings = []

if "libs.versions.toml" in fp:
    warnings.append(
        "⚠️  CATALOG GUARD: libs.versions.toml change detected.\n"
        "   All dependency versions cascade from here. Verify:\n"
        "   - Kotlin ↔ Compose BOM ↔ AGP compatibility matrix\n"
        "   - GeckoView version pinned to Mozilla Maven\n"
        "   - Run: ./gradlew dependencies | grep -i conflict"
    )

if "AndroidManifest.xml" in fp:
    warnings.append(
        "🔐 MANIFEST GUARD: AndroidManifest.xml change detected.\n"
        "   Verify before saving:\n"
        "   - No unintended android:exported=\"true\" on services/receivers\n"
        "   - LEANBACK_LAUNCHER intent filter is intact\n"
        "   - QUERY_ALL_PACKAGES still present (required for app grid)\n"
        "   - INTERNET still present (required for browser + widgets)"
    )

if "proguard-rules.pro" in fp:
    warnings.append(
        "🔒 PROGUARD GUARD: proguard-rules.pro change detected.\n"
        "   MinifyEnabled is currently false in release. If enabling:\n"
        "   - Add GeckoView keep rules\n"
        "   - Add Hilt keep rules\n"
        "   - Test release variant: ./gradlew assembleRelease"
    )

if "local.properties" in fp:
    warnings.append(
        "🔑 SECRETS GUARD: local.properties is git-ignored and local only.\n"
        "   NEVER commit this file. Store API keys here (NASA, weather, etc)."
    )

if "gradle.properties" in fp and not "local" in fp:
    warnings.append(
        "⚙️  GRADLE PROPS GUARD: gradle.properties is committed to git.\n"
        "   Do NOT add API keys or secrets here. Use local.properties."
    )

if warnings:
    print("\n".join(warnings), file=sys.stderr)
