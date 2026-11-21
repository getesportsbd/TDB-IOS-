# TDB iOS Experience (v2.0)

A polished, native **Kotlin & Jetpack Compose** Android home experience and utility suite (`com.tdb.ios.experience`, Android 6.0 / API 23+) inspired by the clean, luminous aesthetic of iOS.

> **Transparency Note:** This is a native Android application built with official Android SDK APIs. It recreates the visual experience, gestures, and layout patterns of an iPhone without modifying Android's underlying OS or using proprietary Apple assets.

## Implemented Features
- **iOS-Inspired Home Screen & Dock**: Multi-page horizontal home grid with spring-animated rounded squircle icons, page indicator dots, and a frosted translucent bottom Dock.
- **Installed App Detection & Launching**: Real `PackageManager` query (`<queries>` manifest filter) detecting installed Android apps, launching them cleanly, and updating dynamically.
- **App Drawer & App Library**: Searchable A–Z App Drawer plus categorized App Library folders (Productivity, Social, Media, Utilities, Creativity, System).
- **Spotlight-Style Search**: Instant filtering across installed Android apps, built-in tools, and system shortcuts, with web search fallback.
- **Dynamic Island Interface**: Interactive top pill displaying live battery/charging status, active Focus Timer, and Flashlight toggle with spring expand/collapse physics.
- **Control Center**: Frosted glass quick panel with real **Window/System Brightness** slider, real **AudioManager Media Volume** slider, **Flashlight** (`CameraManager`), and native Android shortcuts for **Wi-Fi**, **Bluetooth**, **Display**, and **Sound**.
- **Lock Screen & Notification Center**: Full-bleed wallpaper Lock Screen with live clock, date, widgets strip, flashlight/camera quick actions, swipe/tap unlock, and Notification Center sheet with system notification shade shortcut.
- **iOS-Inspired Widgets**: Live Battery & Device Health, Calendar & Quick Reminders (DataStore-persisted), Clock, Atmosphere/Weather, and Focus Timer.
- **Wallpaper Customization**: 4 built-in original abstract light wallpapers plus zero-permission Android Photo Picker (`PickVisualMedia`) custom photo support.
- **Premium Settings Dashboard**: Grouped light-mode settings console with real device telemetry, layout controls, Default Home Launcher shortcut (`ACTION_HOME_SETTINGS`), and system preferences.

## Build & CI
Build locally with:
```bash
gradle assembleDebug
```
The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

GitHub Actions (`.github/workflows/android.yml`) automatically runs `gradle assembleDebug` on `ubuntu-latest` with JDK 17 and uploads `app-debug.apk` as a workflow artifact.
