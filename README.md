# Photobesok Studio 0.5

Native offline Android photobooth for Android 10+. Original interface in forest, ivory and gold.

## Guest journey
Welcome → choose one of four photo layouts and three frame colors → automatic multi-photo camera session → review / retake → save PNG to Pictures/Photobesok → share or start the next session.

## Operator settings
Event caption, countdown (2–10 seconds), front/back default camera, mirrored output, persistent PNG overlay, printer IP, TCP 515 connectivity check and Android print settings. The guest journey does not expose technical form fields.

## Printing
Output is 1200×1800 PNG (4R at 300 dpi). Printing uses the installed Android print service. Native Epson LPR raster encoding is not implemented; port 515 is a TCP connectivity check only. Physical printer validation remains required.

## Design references
- https://docs.pixture.io/id/docs/tutorials/customise-kiosk-screens — separated guest screens and template/capture/review flow.
- https://thephotoboothapp.com/how-the-photo-booth-app-works/ — event setup, guided capture, review and local results.
No third-party artwork or app code copied. Layout illustrations are original Canvas drawings.

## Validation
GitHub Actions compiles the APK, runs Android lint, and launches an Android 10 tablet emulator. scripts/ui_smoke.py exercises welcome, layout, 3 automatic captures, review, MediaStore export, next session and operator settings, capturing real screen evidence. No production test hooks are enabled. Emulator validation does not replace a Xiaomi Pad 7 / Epson L8050 test.

## Build
JDK 17, Android SDK 35, Gradle 8.9: `gradle :app:assembleDebug :app:lintDebug`.
Initial dependency download needs internet; capture, layout and save do not. Debug builds are for testing. USB DSLR, payment, cloud galleries and native LPR are not included.
