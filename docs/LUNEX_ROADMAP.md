# Lunex Keyboard • development roadmap

## Source and license
Based on HeliBorg/HeliBoard, GNU GPL-3.0-only, source commit 106386356f0ff5b13ab6617bca4d1cea4d9513e6 (2026-11-17). Keep all upstream copyright notices and license files. This is an independently branded personal fork, not affiliated with Apple, Yandex or the upstream maintainers.

## First milestone
- Standalone Android package `ai.lunex.keyboard`, preserving HeliBoard IME functionality
- Two selectable, rounded, high-contrast Liquid Glass-inspired themes, automatically following light/dark OS mode
- Built-in HeliBoard clipboard history, pinning and suggestion settings, no remote account or analytics
- Touch and gesture typing pipeline untouched from upstream
- Android native build via Gradle/Android SDK, no Godot

## Limitations and next tasks
- The first glass themes are **opaque, glass-inspired surfaces**, not system-behind-window blur. True blur is not guaranteed for an IME on all Android versions and must have a low-cost fallback.
- Glide typing is **not built into HeliBoard**; it requires a separately supplied compatible proprietary library. Do not bundle an unlicensed gesture library. Validate Russian/English gesture support on the actual phone.
- Evaluate a wholly open swipe recognizer if the proprietary module is unavailable.
- Add enhanced clipboard search, optional retention, encrypted pinned history and a secure-mode privacy audit.
- Review translations, branding/icons, accessibility, dynamic sizes and high-contrast mode.
- Test on a physical phone before considering any release stable.

## Acceptance tests
1. The IME can be enabled/selected without replacing an existing keyboard.
2. Cyrillic and Latin layouts, autocorrection, spacing, Enter, backspace and emoji work.
3. Colors/themes switch with day/night, keys remain readable on white and black application backgrounds.
4. Clipboard pin/delete operations work; passwords and incognito modes do not leak into clipboard history.
5. Build produces a debug APK with application ID `ai.lunex.keyboard.debug`.
6. No network permission added; no personal clipboard data written into logs.

## Build safety
Use isolated project directory, user `gamedev`, existing Android SDK when available; do not modify other VPS projects. Upstream source remains GPL-3.0-only. No production signing key is stored in git.
