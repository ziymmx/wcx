# WeKit — Agent Guide

## Build

```bash
./x build           # debug (uses same signing as release)
./x build --release # release (with optimization on)
# (./x is alias to `cargo xtask` which orchestrates the build process)
```

- JDK 21. `gradle/libs.versions.toml` says 17 and that is what app/reflekt/stubs
  compile against, but `libs/common/bsh` hardcodes `JavaVersion.VERSION_21`, so the
  build needs a JDK 21 toolchain. (Wrapper is Gradle 9.6.1, AGP 9.3.0.)
- Rust native lib auto-compiles during build (targets: `app/src/main/rust/wekit-native`). Requires:
  Note: the CI workflow does **not** build it — `app/src/main/jniLibs/arm64-v8a/libwekit_native.so`
  is committed, so CI skips the whole Rust/NDK setup. Only re-run `cargo xtask build --native-only`
  if you change Rust sources.
  Rust toolchain + Android NDK targets + NDK. `configureCargo` task auto-generates `.cargo/config.toml`
  from NDK.
- AGP 9, Gradle version catalog in `gradle/libs.versions.toml`

## Project Structure

- `app/` — main Android module, entrypoints, hooks, UI, native Rust lib
- `libs/common/annotation-scanner/` — KSP annotation processor (`@Feature` scanner)
- `libs/common/libxposed-api/` — compileOnly LibXposed API interface stubs (compileOnly since they are provided by user's Xposed framework)
- `libs/common/bsh/` — git submodule (`Johnny520/bsh`): forked BeanShell interpreter with snapshot serialization (`BshSnapshot`, `BshSnapshotHelper`); snapshots are encrypted AST byte representations used by the WAuxiliary Xposed module; `app/src/main/java/dev/ujhhgtg/wekit/utils/BshSnapshotDecompiler.kt` — decompiles encrypted BeanShell snapshot files back into Java-like source code; the AES key was recovered from WAuxiliary's decompiled source
- `libs/common/reflekt/` — git submodule (`Ujhhgtg/reflekt`): reflection utility library (`dev.ujhhgtg.reflekt`). **Required** — `SpoofEnvironment`, `HideModuleFromAppList`, `DisableHostHotUpdates` and `ForceTabletMode` all call `.reflekt()`
- `features.whitelist` — **build-time feature filter** (repo root). See "Feature Whitelist" below
- `libs/common/stubs/` — compileOnly stubs for WeChat and Android hidden classes
- `buildSrc/` — custom Gradle tasks: `GenerateMethodHashesTask` (`IResolveDex` `resolveDex` method MD5 cache), `ConfigureCargoTask` (Rust NDK linker config)

## Entry Points & Architecture

- Xposed entry: `com.Johnny.wcx.loader.entry.lsp10x.Lsp10xUnifiedHookEntry` (libxposed 101 & 100) and legacy Xposed API (51+) entry: `com.Johnny.wcx.loader.entry.xp51.Xp51HookEntry`
- Unified flow: `UnifiedEntryPoint.entry()` → `StartupAgent.startup()` → `WeLauncher.init()`
- Hook items annotated with `@Feature(path, description)`, auto-discovered by KSP annotation scanner at compile time
- Base classes: `SwitchFeature` (toggle on/off), `ClickableFeature` (toggle on/off with onClick event), `ApiFeature` (always-on), `BaseFeature` (abstract base, do not use directly)
- DEX analysis via DexKit with `IResolveDex` interface; method resolve body MD5-hashed for cache (
  `GenerateMethodHashesTask`)
- DEX-resolved targets DSL: `val methodTarget by dexMethod()` `val classTarget by dexClass()` delegate → `methodTarget.hookBefore { ... }`, `val method: Method = methodTarget.method`, `val clazz = classTarget.clazz`

## Feature Whitelist

This fork builds a small curated feature set. `features.whitelist` (repo root) lists the
Kotlin **class simple names** to keep, one per line (`#` starts a comment). It is passed to
the KSP processor via `arg("wekit.feature.whitelist", ...)` in `app/build.gradle.kts`.

How it works: `FeaturesScanner` filters the `@Feature` symbols before emitting
`FeaturesProvider.ALL_HOOK_ITEMS`. Excluded features are still compiled but become
unreachable, so R8 strips them from the APK. Removing the `arg(...)` (or the file)
restores the full upstream feature set.

**Rules:**
- Match on class simple name, not the Chinese `name` argument (names can collide).
- Features under `features/api/` are shared services and are **not** auto-included.
  Adding an item feature that depends on one requires listing that service too —
  e.g. `AntiMessageRecall` needs `WeXmlParserApi`, `WeDatabaseApi`, `WeMessageApi`,
  and `WeMessageApi` in turn needs `WeNetSceneApi`.
- Deleting feature files is NOT the way to trim this project — the API layer has
  dense cross-dependencies. Filter instead.
- **`app/proguard-rules.pro` must stay in sync with the whitelist.** Upstream had a
  blanket `-keep class com.Johnny.wcx.features.** { *; }` which keeps every feature
  class regardless of reachability, so R8 stripped nothing and the whitelist had no
  effect on APK size. The rule is now an explicit per-class list; when you add a
  feature to `features.whitelist`, add a matching `-keep` line or it may be
  obfuscated/shrunk away.
- `SwitchFeature.defaultEnabled` is `true` in this fork: every whitelisted feature is
  on at startup with no in-WeChat settings entry, so there is no UI to toggle them.
  The standalone module app (`MainActivity`) still lists them if you need a manual override.
- UI: Jetpack Compose + Material 3, dialogs written using `showComposeDialog` and `AlertDialogContent`
- Config: MMKV via `WePrefs`
- Logging: via `WeLogger`

## Key Conventions

- Package namespace: `com.Johnny.wcx`
- Min SDK 29, target SDK 37, compile SDK 37
- Target: WeChat `com.tencent.mm`, versions 8.0.65–8.0.71. Version info in `HostInfo`
- Process targeting via `TargetProcesses`: override `startup()` to check
  `TargetProcesses.isInMain` / `TargetProcesses.currentType`. Default: main process only.
- No unit tests — manual testing on real WeChat only
- If `JsApiExposer` (`hooks/items/scripting_js/JsApiExposer.kt`) is modified, keep `globals.d.ts` in
  the same directory in sync — it's the TypeScript type declaration for the JS scripting API
- NEVER wrap `hookBefore` and `hookAfter` in a `try-catch`/`runCatching` block. They should NOT fail. If they fail, then it's the module developer's problem.
- Use `WePrefs.Companion.prefOption` delegates to declare & use preference items easily.

## Naming Conventions

- 群聊: WeChat: chatroom; WeKit: group/群组
- 朋友圈: WeChat: sns; WeKit: moment

## Context you need

- WeChat decompiled sources: ~/coding/wechat_8074
- Decrypted WeChat main database: ./decrypted_wechat.db

## CI

- GitHub Actions: builds on push/PR to `master` (skips non-code changes)
- Artifacts automatically published to a release named "CI" + Telegram channel
