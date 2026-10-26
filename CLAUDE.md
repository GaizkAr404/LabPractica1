# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Practica1 is a single-module Android app (`:app`), written in **Java** (not Kotlin) with XML layouts (Views, not Compose). Package/namespace: `com.urjc.practica1`. It is currently the Android Studio "Empty Views Activity" template: one `MainActivity` that inflates `res/layout/activity_main.xml`.

## Commands

Use the Gradle wrapper from the repo root:

```bash
./gradlew assembleDebug                 # build debug APK
./gradlew installDebug                  # build + install on a connected device/emulator
./gradlew test                          # JVM unit tests (app/src/test)
./gradlew connectedAndroidTest          # instrumented tests (app/src/androidTest), needs device/emulator
./gradlew lint                          # Android lint

# Single test class / method
./gradlew :app:testDebugUnitTest --tests "com.urjc.practica1.ExampleUnitTest"
./gradlew :app:testDebugUnitTest --tests "com.urjc.practica1.ExampleUnitTest.addition_isCorrect"
./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.urjc.practica1.ExampleInstrumentedTest
```

## Build setup

- AGP 9.x with Kotlin DSL build scripts; dependency and plugin versions live in the version catalog `gradle/libs.versions.toml` — add new dependencies there and reference them as `libs.<alias>` in `app/build.gradle.kts`.
- `compileSdk`/`targetSdk` 37, `minSdk` 24, Java 11 source/target.
- Release builds enable R8 optimization (`optimization { enable = true }`).
- Dependencies: AppCompat, Material Components, ConstraintLayout, activity-ktx. Tests: JUnit 4, AndroidX Test (ext-junit, Espresso).

## Architecture notes

- `MainActivity` extends `AppCompatActivity`, calls `EdgeToEdge.enable(this)` and applies system-bar insets as padding to the root view with id `main`. Keep that root id (or update the listener) when editing the layout.
- App theme is `Theme.Practica1` (`res/values/themes.xml`, with a `values-night` variant). New activities must be registered in `AndroidManifest.xml`.
