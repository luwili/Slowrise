# Slowrise

**A gradual wake-up alarm app for Android.**

Slowrise is an Android alarm clock designed to make waking up more comfortable with gradually increasing audio volume.

The alarm starts playing 30 minutes before the selected wake-up time and smoothly increases its volume until it reaches 100%.

## Features

- **Gradual volume increase** — volume rises smoothly from 5% to 100% over 30 minutes.
- **Multiple wake-up sounds** — choose from 16 high-quality 1-hour ambient soundscapes (nature, rain, ocean, campfire, piano, lo-fi, etc.).
- **In-app sound selector & preview** — listen to and pick your favorite sound directly in the app.
- **Custom alarm audio** — audio files are bundled directly into the application.
- **Background playback** — alarm playback is handled by a foreground service.
- **Lock screen support** — playback can continue when the screen is locked.
- **Daily scheduling** — the alarm automatically schedules itself for the next day.
- **Persistent settings** — alarm time, enabled state, and selected melody are saved.
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

### Installing the fork

Since v3.1.2, this fork uses the separate application ID `com.luwili.slowrise`, so it can be installed alongside the original author's app without replacing it or deleting its data. Future releases of the fork keep this ID and the project's signing key, so they can update the fork in place. Earlier fork releases used the author's application ID and cannot be updated into this separate app; their settings and alarms do not transfer automatically.

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

## Audio Assets

The alarm audio tracks are stored in:

```text
app/src/main/res/raw/
```

Tracks include 16 1-hour ambient soundscapes (original `slowrise_audio.mp3` plus 15 curated nature and musical tracks: birds, rain, ocean waves, mountain stream, fireplace, summer night, wind, warm piano, lofi beats, ambient synth, acoustic guitar, forest, storm, calm boat, train).

## Project Status

Slowrise is an actively developed personal project. Features and implementation may change as development continues.

## Русский

**Slowrise** — Android-будильник для мягкого пробуждения.

Приложение запускает звук за 30 минут до установленного времени и постепенно увеличивает громкость от 5% до 100%.

- Включает 16 встроенных часовых звуковых дорожек (шум дождя, пение птиц, прибой, ручей, камин, фортепиано, лоу-фай и др.).
- Возможность выбора и предпрослушивания мелодии прямо в приложении.

Проект разрабатывается на Kotlin с использованием Jetpack Compose.

---

**Repository:** https://github.com/SenyaTaush/Slowrise
