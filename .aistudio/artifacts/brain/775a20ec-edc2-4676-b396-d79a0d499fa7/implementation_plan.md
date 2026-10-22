# VIP All Social Media Video Downloader (VidMax VIP)

A luxury, glassmorphic Android app designed to parse, preview, and download videos and audio from all major social media platforms (TikTok, Instagram, Facebook, Twitter/X, Pinterest, Vimeo, YouTube shorts/clips, and direct web links) with multi-resolution selection (1080p, 720p, 480p, 360p, MP3/M4A 320kbps), an in-app HD media player, built-in social browser with media sniffer, and real-time download manager.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed and embedded into this architectural blueprint:

- **Confirmed Aesthetic**: Premium VIP dark mode featuring glassmorphism, frosted glass card containers with subtle borders, warm orange and amber ambient glow highlights, and glowing pill controls. Light mode toggle included with sleek champagne/cream aesthetics.
- **Confirmed Feature 1 (Clipboard Auto-Detect)**: The app monitors the clipboard on launch and resume; when a compatible social video link is detected, a floating VIP Preview Sheet automatically animates upward with video title, thumbnail, duration, and one-tap download resolution selector.
- **Confirmed Feature 2 (Built-in Media Player & Browser)**: Includes a full-screen hardware-accelerated video & audio player with playback speed, seek bar, loop, and PiP controls, plus an integrated Social Hub Browser to browse platforms directly and sniff playable video streams.
- **Durable Local Storage**: Uses Android Room Database for saving downloaded file history, download task states (active, paused, completed, failed), custom tags, and metadata.

---

## 1. Overview & Core Concept

- **What It Does**: VidMax VIP is an all-in-one social media video and audio extraction studio. Users can either paste a link, allow clipboard auto-detection, or browse their favorite social platforms in the integrated browser. The app extracts media titles, thumbnails, author info, and available quality streams (MP4 1080p, 720p, 480p, 360p, or high-fidelity MP3/M4A audio) and queues them in a multi-threaded background download manager with real-time progress, speed, and notification controls.
- **Target Audience / Persona**: Content creators, social media curators, offline viewers, and music lovers who need a reliable, high-speed downloader with a VIP aesthetic and no cumbersome redirects or third-party players.
- **Key Value**:
  - Instant one-tap extraction with rich metadata preview before downloading.
  - Video (MP4) to Audio (MP3) extraction with customizable bitrates (320kbps, 192kbps, 128kbps).
  - Built-in media player with gesture controls and background audio playback.
  - Local privacy: all files saved directly into the device's standard `Movies/VidMax` and `Music/VidMax` directories.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Paste & Download Flow**:
   - User launches app or switches back with a copied link -> Floating VIP banner: *"Video link detected from Instagram: Tap to preview"*.
   - Tap opens the **VIP Media Inspection Dialog** showing animated thumbnail, video title, author, duration, and stream selector chips (`1080p HD [48 MB]`, `720p [26 MB]`, `480p [14 MB]`, `MP3 Audio [4.2 MB]`).
   - Tapping "Download Now" starts immediate asynchronous download with real-time progress ring and speed gauge.
2. **Download Manager & Queue Flow**:
   - Bottom tab `Downloads` displays active and completed files with category filters (`All`, `Videos`, `Audio`).
   - Provides pause, resume, cancel, delete, and share intents.
3. **Integrated Player Flow**:
   - Tapping any completed item launches the in-app VIP Player with custom transport controls, orientation toggle, aspect ratio changer, and track info.
4. **Social Explorer Browser Flow**:
   - Quick launch bookmarks (Instagram, TikTok, Facebook, Twitter, Pinterest, Reddit).
   - Bottom floating "Sniff Media" button pulses golden amber whenever a downloadable video stream is detected on the active webpage.

### Visual Identity & Theme
- **Color Palette (Dark Mode - VIP Amber & Obsidian)**:
  - Canvas Background: `0xFF0D0D12` (deep obsidian midnight)
  - Ambient Glows: `0xFFFF9F1C` (radiant warm amber) and `0xFFFF6B35` (golden sunset) radial brush highlights
  - Glass Card Surfaces: `0x26FFFFFF` (15% frosted translucent white with 1dp border `0x33FFA726`)
  - Accent / Primary CTA: Gradient from `0xFFFF9800` to `0xFFFF5722` with soft drop-shadow glow
  - Text Primary: `0xFFF5F5F7`, Text Secondary: `0xFF9E9EA7`
- **Color Palette (Light Mode - Champagne Pearl)**:
  - Canvas Background: `0xFFF8F7F4` (warm ivory pearl)
  - Card Surfaces: `0xFFFFFFFF` with delicate amber border `0x33FF9800`
  - Accent: `0xFFE65100` deep amber orange
- **Typography**: Clean modern typography hierarchy with high-contrast numerical stats (file size, speed in MB/s, resolution badges).
- **Motion & Micro-interactions**:
  - Spring-animated resolution selection chips.
  - Smooth pulsating glow on download buttons during active extraction.
  - Animated progress bars with gradient shimmer.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct Universal Social Link Parser & Extractor Engine**
  - *Chosen Approach*: A hybrid extraction architecture. Recognizes URL patterns for Instagram (Reels/Stories/Posts), TikTok, Facebook, Twitter/X, Pinterest, Vimeo, Reddit, and direct MP4/HLS streams. It extracts OpenGraph metadata (og:video, og:title, og:image) and video stream URLs with resilient fallbacks, alongside direct demo test streams for instant offline testing and verification.
  - *Why*: Allows instantaneous offline and online extraction without reliance on volatile third-party paid scraping proxies that frequently break.
- **Decision 2: Download Architecture via Android DownloadManager & OkHttp Engine**
  - *Chosen Approach*: Combined OkHttp streaming with Android `Environment.DIRECTORY_MOVIES` / `Environment.DIRECTORY_MUSIC` standard storage integration and fallback foreground coroutine downloader with chunked progress reporting.
  - *Why*: Gives complete control over progress bytes, transfer rate (MB/s), pause/resume states, and guarantees files appear in Android's native gallery/music apps.
- **Decision 3: Room Database for Media & Task Persistence**
  - *Chosen Approach*: SQLite Room Database storing `DownloadItemEntity` (id, title, url, localFilePath, thumbnail, mediaType, resolution, totalBytes, downloadedBytes, status, createdAt).
  - *Why*: Fast, crash-resilient, offline-first, perfectly suited for device-local media management.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          VidMax VIP App UI                             │
├────────────────────────────────────────────────────────────────────────┤
│ ┌────────────────┐ ┌───────────────────┐ ┌───────────────────────────┐ │
│ │  Home Screen   │ │ Downloads Manager │ │   Social Browser Tab      │ │
│ │  - URL Input   │ │ - Active queue    │ │   - Quick bookmarks       │ │
│ │  - VIP Preview │ │ - Completed list  │ │   - Stream sniffer        │ │
│ │  - Format Selector│ - Play/Share/Del  │ │   - One-tap download      │ │
│ └───────┬────────┘ └─────────┬─────────┘ └─────────────┬─────────────┘ │
│         │                    │                         │               │
│         ▼                    ▼                         ▼               │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │                      VIP Media Player Dialog                       │ │
│ │ - Video playback (ExoPlayer/SurfaceView) | MP3 audio waveform view │ │
│ └────────────────────────────────────────────────────────────────────┘ │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        ViewModels & Repositories                       │
├────────────────────────────────────────────────────────────────────────┤
│  DownloaderViewModel           BrowserViewModel     PlayerViewModel    │
│  - Link detection              - Web navigation     - Playback state   │
│  - Stream extraction           - Stream detection   - Position/Volume  │
├────────────────────────────────────────────────────────────────────────┤
│                       DownloadManagerRepository                        │
│  - OkHttp progressive stream engine with byte rate tracking            │
│  - Multi-resolution stream selector & MP3 audio extractor pipeline     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                  ┌─────────────────┴─────────────────┐
                  ▼                                   ▼
┌───────────────────────────────────┐ ┌───────────────────────────────────┐
│       Room Database (SQLite)      │ │        Android File System        │
│ - DownloadItemEntity              │ │ - /Movies/VidMax/{title}.mp4      │
│ - Task status, speed, file paths  │ │ - /Music/VidMax/{title}.mp3       │
└───────────────────────────────────┘ └───────────────────────────────────┘
```

### Component Breakdown
1. **`MainActivity.kt`**: Single-activity host with window insets, clipboard listener lifecycle, and bottom VIP navigation bar.
2. **`ui/screens/HomeScreen.kt`**: Link input bar with Paste button, platform shortcut badges, clipboard detection banner, extraction preview card with thumbnail, and resolution/format selector dialog.
3. **`ui/screens/DownloadsScreen.kt`**: Tabbed view of Active and Completed downloads with progress rings, download speed indicator (e.g. `4.2 MB/s`), pause/resume/cancel controls, and file management actions.
4. **`ui/screens/BrowserScreen.kt`**: Integrated WebView for social platforms with address bar, navigation controls, and floating "Download Media" sniffer FAB.
5. **`ui/components/MediaPlayerModal.kt`**: In-app video/audio player overlay with full transport controls, slider scrubbing, and PIP support.
6. **`data/` & `domain/`**: Room database, DAO, entities, link extraction engine (`SocialMediaExtractor`), and download service.
