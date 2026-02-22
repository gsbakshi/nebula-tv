---
name: install
description: Build and install the Nebula debug APK to a connected Android TV device. Checks for device connectivity and explains how to connect if none found.
---

Build and install to a connected Android TV:

```bash
./gradlew installDebug
```

Before reporting results, handle these cases:

## No Device Found
If the install fails with "no connected devices":
1. **Network ADB** (most common for TV):
   ```bash
   adb connect <tv-ip-address>:5555
   ```
   Find TV IP: Android TV Settings → Network → Your network → Advanced → IP address
   Then enable: Settings → Device Preferences → Developer options → Network debugging

2. **USB ADB**:
   Connect USB cable → Developer options → USB debugging → Allow this computer

3. **Verify**: `adb devices` should list the TV

## Successful Install
Report:
- Device name and Android version
- APK size installed
- How to launch directly: `adb shell am start -n com.example.nebula/.MainActivity`
- Note: To use as actual launcher, go to TV Settings → Apps → Default apps → Home app → Nebula

## Multiple Devices
If multiple devices connected, `installDebug` installs to all. To target one:
```bash
./gradlew installDebug -Pandroid.targetDeviceSerialNumber=<serial>
```
Get serial from: `adb devices`

## After Install: Quick Sanity Checks
Suggest these quick verifications after install:
1. App grid loads (PackageManager query working)
2. Navigation bar D-pad works (left/right between tabs)
3. App launches when pressing OK on an app card