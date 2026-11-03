# Photobesok Offline Learning 0.4

Android 10+ learning app for Xiaomi Pad 7. Native, local photo workflow; no account, payment, analytics or cloud runtime. This is an original implementation, not a patched OurBooth APK.

## Features
- Welcome screen and local session editor, 1–4 photo layouts.
- Forest, Gold and White built-in frames; optional user-supplied PNG overlay.
- CameraX front/back camera with 3-second timer; EXIF-aware photo decoding.
- PNG export to Pictures/Photobesok via MediaStore.
- Saved printer IP and TCP port 515 connectivity check.
- Android print service with paper-aware PDF scaling and cancellation.

## Printing limitation
The port 515 button tests a TCP connection only. Native LPR print jobs and Epson raster encoding are NOT implemented. Android printing requires a compatible installed print service. A successful port test does not prove printable data compatibility. Validate output on the actual L8050 before event use.

## Three-plugin collaboration
- Library: consulted `laporan-inventaris-ourbooth.txt` to understand the prior asset inventory. No OurBooth binary, graphics or proprietary code is bundled.
- Canva: editable visual concept https://canva.link/5dgqyqu2tm1cvyr . Android UI uses native controls, not a remote Canva page.
- GitHub: source history and Actions compilation/lint; artifact `Photobesok-Offline-APK` contains the installable debug APK.

## Build
JDK 17, Android SDK 35, Gradle 8.9. Run `gradle :app:assembleDebug :app:lintDebug`. Internet is required for the initial build dependencies, not normal app use. The debug APK is for testing, not a production release.

## Device acceptance checklist
1. Deny camera permission, then grant it; verify graceful return and successful capture.
2. Test both cameras, timer, background/resume, and JPEG orientation.
3. Complete 1, 2, 3, 4 photos; change layout and confirm session resets.
4. Test all frame styles, PNG import/removal, invalid dimensions and gallery export.
5. Disable internet; verify capture, editing and gallery export still work.
6. Join printer Wi-Fi, test reachable/unreachable port 515, then print using the installed Android service. Check physical 4R dimensions and cropping.

No device or physical printer test has been performed by this build environment. Capture is one photo per camera visit; full automatic multi-shot, USB DSLR, payments and native LPR remain outside this version.
