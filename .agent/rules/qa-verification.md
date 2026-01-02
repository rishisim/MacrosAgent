---
trigger: always_on
---

# Post-Implementation Verification Protocol

**Goal:** Verify that code changes actually work on the connected Android device using the `android-tooling` MCP server.

**Trigger:** After generating code for a bug fix or new feature.

**Protocol:**
1.  **Launch:** Use `execute_adb_shell_command` to run `am start -n <your.package.name>/.MainActivity` (or the relevant activity).
2.  **Analyze UI:** Call `get_uilayout` to parse the screen. Identify the clickable elements relevant to the feature you just built.
3.  **Interact:**
    * If you need to click a button, calculate its center coordinates from the bounds provided in `get_uilayout` and use `execute_adb_shell_command` with `input tap <x> <y>`.
    * If the feature involves intents, use `get_package_action_intents` to verify registration.
4.  **Confirm:** Finally, run `get_screenshot` to capture the state of the app and present it as proof of functionality.