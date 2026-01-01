---
description: Refactor Android App to Clean Architecture & Hilt
---

# Android Refactoring Workflow

This workflow guides the process of refactoring a legacy Android app (Monolithic Activity) to Modern Android Development (MAD) standards.

## 1. Setup Dependency Injection (Hilt)
- [ ] Add Hilt dependencies to `libs.versions.toml`.
- [ ] Apply `com.google.dagger.hilt.android` plugin to Project and App `build.gradle.kts`.
- [ ] Create `Application` class annotated with `@HiltAndroidApp`.
- [ ] Update `AndroidManifest.xml` to use the new Application class.

## 2. Layer Separation
- [ ] **Domain**: Create pure Kotlin modules/packages for business logic (Use Cases, Models).
- [ ] **Data**: Implement Repositories and Data Sources.
- [ ] **UI**: Move logic from Activity to ViewModels.

## 3. ViewModel Migration
- [ ] Create `@HiltViewModel` classes.
- [ ] Inject Repositories into ViewModels.
- [ ] Expose `StateFlow` or `Compose State` to the UI.
- [ ] Remove logic from `MainActivity` / Composables (they should only observe state).

## 4. Verification
- [ ] Run Unit Tests: `./gradlew testDebugUnitTest`
- [ ] Build App: `./gradlew assembleDebug`
- [ ] Validate UI flows manually or via Screenshot tests.
