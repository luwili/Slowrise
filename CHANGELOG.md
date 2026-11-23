# Changelog

All notable changes to Slowrise are documented here.

## [3.1.2] - 2026-11-24

### Fixed
- Assigned the fork its own application ID (`com.luwili.slowrise`) so it can be installed alongside the author's app.

### Changed
- Bumped version to 3.1.2 (`versionCode = 6`).
- Kept the project signing key so future releases under the fork's new application ID can update in place.

## [3.1.1] - 2026-11-23

### Fixed
- Kept the package ID and project signing key used by the fork's v3.1 release so v3.1.1 can update it in place.
- Renamed the launcher entry to `Slowrise 3.1 Fork` to distinguish the two installations.

### Changed
- Bumped version to 3.1.1 (`versionCode = 5`).

## [3.1.0] - 2026-11-16

### Added
- 15 new high-quality 1-hour ambient soundscapes in `res/raw`:
  - Nature: Birds & morning forest (`sound_birds`), Pine forest (`sound_forest`), Summer night crickets (`sound_summer_night`), Whispering wind (`sound_wind`).
  - Water: Gentle rain (`sound_rain`), Ocean waves (`sound_waves`), Mountain stream (`sound_stream`), Distant storm (`sound_storm`), Calm boat (`sound_boat`).
  - Music & Ambience: Cozy fireplace (`sound_fireplace`), Warm piano (`sound_warm_piano`), Lo-fi beats (`sound_lofi_beats`), Meditative ambient (`sound_ambient_music`), Acoustic guitar (`sound_acoustic_guitar`), Sunrise train (`sound_train`).
- Interactive sound selector modal bottom sheet (`SoundPickerSheet`) with category labels.
- In-sheet instant preview playback (Play / Pause button) for every sound.
- Selected melody indicator and persistent storage across app launches via `SharedPreferences`.
- Display active melody name and emoji in the foreground alarm notification.
- Bundled shared project keystore for reproducible builds and seamless updates without uninstalling.

### Changed
- Bumped version to 3.1 build 4 (`versionCode = 4`, `versionName = "3.1"`).
- Renamed the installed app to `Slowrise 3.1`.
- Reduced bundled audio size by 49% while preserving all one-hour sound loops.
- Improved sound picker text layout on narrow screens.
- Kept release signing compatible with existing 3.1 installs.
- Synchronized main screen alarm test player with the currently selected melody.

### Fixed
- Protected `MediaPlayer` lifecycle operations against `IllegalStateException` during rapid sound switching and cancellation.
- Limited category labels to one line so they no longer stretch sound cards vertically.
- Updated notification construction to use AndroidX `NotificationCompat`.
- Clarified the raw audio resource annotation for Kotlin.

## [3.0.0] - 2026-11-14

### Added
- First public release of Slowrise.
- Gradual alarm volume increase from 5% to 100% over 30 minutes.
- Automatic daily alarm scheduling.
- Background audio playback using a foreground service.
- Persistent alarm settings.
- Alarm stop action in the notification.
- Built-in one-hour alarm audio.
- Android 10 device compatibility tested.

## Development

Slowrise is actively developed. Future changes, improvements and bug fixes will be documented here.
