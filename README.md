# Slowrise

**A gradual wake-up alarm app for Android.**

Slowrise is an Android alarm clock designed to make waking up more comfortable with gradually increasing audio volume.

The alarm starts playing 30 minutes before the selected wake-up time and smoothly increases its volume until it reaches 100%.

## Features

- **Gradual volume increase** — volume rises smoothly from 5% to 100% over 30 minutes.
- **Custom alarm audio** — audio is bundled directly into the application.
- **Background playback** — alarm playback is handled by a foreground service.
- **Lock screen support** — playback can continue when the screen is locked.
- **Daily scheduling** — the alarm automatically schedules itself for the next day.
- **Persistent settings** — alarm time and enabled state are saved.
- **Stop notification** — playback can be stopped from the notification.

## Tech Stack

- Kotlin
- Jetpack Compose
- Android SDK
- Android Studio
- Gradle

## Requirements

- Android 8.0 (API 26) or newer
- Android Studio
- Android SDK

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/SenyaTaush/Slowrise.git
```

### 2. Open the project

Open the cloned `Slowrise` folder in Android Studio and wait for Gradle Sync to finish.

### 3. Build the application

On Windows:

```bash
gradlew.bat assembleDebug
```

Or use **Build → Build Bundle(s) / APK(s) → Build APK(s)** in Android Studio.

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Audio Asset

The alarm audio is stored in:

```text
app/src/main/res/raw/slowrise_audio.mp3
```

The audio file is bundled into the APK and is required for the application to work.

## Project Status

Slowrise is an actively developed personal project. Features and implementation may change as development continues.

## Русский

**Slowrise** — Android-будильник для мягкого пробуждения.

Приложение запускает звук за 30 минут до установленного времени и постепенно увеличивает громкость от 5% до 100%.

Проект разрабатывается на Kotlin с использованием Jetpack Compose.

---

**Repository:** https://github.com/SenyaTaush/Slowrise