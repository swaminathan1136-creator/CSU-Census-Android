# CSU Census Android – UI updated to match iOS

This package contains the Android app source with UI adjustments so the look & feel closely matches the iOS SwiftUI app.

## Changes made

### Colors (`res/values/colors.xml`)
- Updated to exact RGB values from iOS `CSUTheme.swift`
  - navy: #0E2542
  - blue: #255DB0
  - background / cream: #F2F6FA
  - light_blue: #E6F0FB
  - green, pink, orange, purple, muted text matched

### Drawables
- `bg_progress_card.xml` – new gradient (blue → navy) for the campaign progress card (same as iOS)
- `bg_card.xml`, `bg_stat_card.xml`, `bg_input.xml`, `bg_avatar.xml`, `bg_primary_button.xml` – corner radii, strokes and colors aligned with iOS

### Layouts
- **fragment_dashboard.xml**
  - Avatar on the **left** (iOS style)
  - Gradient progress card with “CURRENT CAMPAIGN”, large %, white progress bar, checkmark count
  - Activity section header with subtitle
  - 2×2 stat cards with secondary labels
  - Quick action grid kept, visual polish
- **activity_login.xml**
  - “COLLECTOR PORTAL” label under logo
  - “Welcome Back!” title matching iOS
  - Card + input styling aligned
- Background color switched from cream to the new light background everywhere

### Themes
- Status / navigation bar and window background use the new background color

## How to use

1. Replace the corresponding files in your existing Android project (`app/src/main/...`).
2. Or open this folder as the project root (you may need to copy `gradle/` wrapper from the original project if missing).
3. Sync Gradle and run.

The Java/Kotlin logic is unchanged – only resources and layouts were updated for visual parity with the iOS app.

Demo login remains: **CSU001** / **Demo@1234**
