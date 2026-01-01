---
description: Build, Deploy, and Debug Android Applications
---

# Android Development Workflow

This workflow describes the standard procedures for building, deploying, and debugging the MacrosAgent application using Gradle and Android MCP tools.

## 1. Building the Application
Use Gradle to build the application. Always use the wrapper.

```bash
./gradlew assembleDebug
```
// turbo
```bash
echo "Build complete."
```

## 2. Installing the Application
After building, install the APK to the connected device.

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 3. UI Inspection & Debugging
Use the Android MCP tools to inspect the running application.

### Take a Screenshot
Capture the current device screen to verify UI state.
- **Tool**: `mcp_android_tooling_get_screenshot`

### Analyze UI Layout
Get a semantic dump of the current UI nodes (clickable elements, text, content descriptions).
- **Tool**: `mcp_android_tooling_get_uilayout`

### Execute ADB Commands
Run arbitrary shell commands on the device (e.g., input text, taps).
- **Tool**: `mcp_android_tooling_execute_adb_shell_command`
- **Example**: `input text "hello world"`

## 4. Logging
Read logs from the device.
```bash
adb logcat -d *:E
```

## 5. Common Issues
- **Permission Denied**: Ensure `adb` is in your PATH or use the absolute path.
- **Device Not Found**: Check connection via `adb devices`.
