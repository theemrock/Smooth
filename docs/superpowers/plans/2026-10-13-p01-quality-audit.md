# P01 Quality Audit Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Repair the SMOOTH P01 shell, prove every mandatory acceptance behavior, audit quality/security/integration readiness, and promote the version only if all mandatory acceptance tests pass.

**Architecture:** Keep a single `:app` APK and a small Compose shell. Represent destinations as saveable state, derive displayed version from `BuildConfig`, keep visible copy in resources, and verify user-visible behavior with Compose UI tests on the connected device.

**Tech Stack:** Kotlin 2.2.21, Android Gradle Plugin 8.11.1, Jetpack Compose BOM 2025.08.01, Material 3, JUnit 4, AndroidX Compose UI Test.

**Spec:** User-provided P01 acceptance and extended quality audit requirements (2026-10-13 conversation).

## Global Constraints

- Write only inside `C:\Projects\Smooth`.
- No Git commit, tag, push, device app installation outside Gradle test tasks, or access to other SMOOTH projects.
- Keep one APK; do not integrate Reader, Media, or Dialogue code.
- Add no unnecessary framework, event bus, abstraction, permission, or fake update check.
- Start at `v0.0.01.000` / `1000`; promote to `v0.0.01.001` / `1001` only after every mandatory P01 acceptance test passes.
- Report device-required tests as NOT RUN when no device is available.

## Review Focus

- Recreation/rotation must retain the active destination and selected module.
- System Back and top-app-bar Back must return a detail screen to Home without terminating unexpectedly.
- Large fonts and a phone-sized viewport must keep all eight cards reachable by scrolling.
- Turkish resources must be valid UTF-8 and cover every visible string.
- The Settings version must be the packaged `BuildConfig.VERSION_NAME`, never a duplicate literal.

---

### Task 1: Restore a reproducible build

**Files:** `app/src/main/AndroidManifest.xml`, `app/src/main/java/com/theemrock/smooth/MainActivity.kt`, `gradle/libs.versions.toml`, `app/build.gradle.kts`

- [x] Reproduce each build blocker and record the exact failing Gradle task.
- [x] Apply one minimal root-cause fix at a time; rerun `:app:assembleDebug` after each fix.
- [x] Confirm plugin/dependency compatibility and remove only dependencies proven unused.

### Task 2: Make shell behavior testable and lifecycle-safe

**Files:** production Compose/navigation sources and string resources; tests under `app/src/androidTest`

- [x] Write Compose UI tests for card order, every destination, both back paths, Settings copy/version, recreation, and update-not-implemented disclosure.
- [x] Implement the minimum saveable navigation/resource/version changes required for GREEN.
- [x] Run all connected debug Android tests — 10/10 PASS on Samsung Galaxy S24 Ultra.

### Task 3: Verify presentation and configuration

**Files:** theme sources, resource files, Android test sources

- [x] Add focused tests for dark/light color schemes and English/Turkish resources where behavior can be exercised.
- [x] Exercise phone viewport, large font, and rotation on the target device.
- [x] Audit manifest permissions, exported components, services, build variants, and packaged APK contents.

### Task 4: Run release gates and conditionally promote

**Files:** central Gradle version definition and any version test

- [x] Run unit tests, connected UI tests, `clean`, and a fresh `assembleDebug` at `v0.0.01.000` / `1000`.
- [x] Apply the conditional gate and promote after 2 JVM + 10 instrumented tests PASS.
- [x] Re-run every executable gate, inspect packaged manifest/version data, and verify the APK exists.

### Task 5: Document the actual state

**Files:** `README.md`, `docs/ARCHITECTURE.md`, `docs/MODULE_INTEGRATION.md`, `docs/TEST_REPORT.md`, `.gitignore`

- [x] Document build/run prerequisites, current functionality, limitations, and exact commands.
- [x] Document the single-APK shell boundary and the simplest future Android library-module integration path.
- [x] Record every check with command, result, evidence, counts, APK path, version, risks, and P02 priorities.
- [x] Ensure `.gitignore` excludes local SDK/build/IDE/heap-dump artifacts without hiding source or reports.
