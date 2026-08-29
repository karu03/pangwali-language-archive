# Pangwali Preservation App

An Android application designed for documenting and preserving the **Pangwali language** (a Western Pahari language spoken in Pangi Valley, Himachal Pradesh). This tool allows field researchers to record speakers, manage linguistic datasets, and build a digital lexicon.

## Key Features

- **Speaker Profiles**: Manage metadata for language consultants, including village origin and dialect variants (Sach, Killar, Purthi, Dharwasi).
- **Linguistic Prompts**: Integrated wordlists and Hindi prompts to guide elicitation sessions.
- **High-Quality Recording**: PCM/WAV audio capture with real-time waveform visualization.
- **Lexicon Builder**: Document Pangwali words with IPA transcriptions, translations, and example sentences.
- **Offline First**: All data is stored locally on the device, ensuring it works in remote valley areas with no connectivity.

## Tech Stack

- **UI**: Jetpack Compose (Modern declarative UI)
- **Database**: Room (SQLite wrapper)
- **Concurrency**: Kotlin Coroutines & Flow
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Manual injection via `ViewModelFactory`
- **Audio**: `AudioRecord` API with a Foreground Service for background stability.

## Project Structure

- `app/src/main/java/org/pangwali/preservation/data`: Data layer (Room, Repositories).
- `app/src/main/java/org/pangwali/preservation/audio`: Audio capture and processing logic.
- `app/src/main/java/org/pangwali/preservation/ui`: UI components and screens.

## Architecture & Data Flow

For a detailed technical breakdown of how data flows through the app, please refer to the [Architecture Guide](.artifacts/ff28e68a-d140-4874-97bb-c271f34a70a2/architecture.artifact.md).

## Development

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 35
- Minimum API Level 29 (Android 10)

### Getting Started
1. Clone the repository.
2. Open in Android Studio.
3. Build and run the `:app` module.
