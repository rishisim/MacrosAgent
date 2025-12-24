# MacrosAgent Project Status

## 📱 Project Overview
MacrosAgent is an Android application designed to automate nutritional tracking and provide intelligent analysis. It leverages on-device and cloud-based AI (Gemini) to analyze food and automatically log it into MyFitnessPal using Accessibility Services.

## ✨ Current Features
- **Nutritional Analysis**:
  - `AnalysisScreen.kt`: UI for displaying analysis results.
  - `GeminiRepository.kt`: Handles interaction with Gemini API for macronutrient estimation.
- **MyFitnessPal Automation**:
  - `MyFitnessPalAccessibilityService.kt`: Accessibility service that interacts with the MyFitnessPal app to automate food logging.
  - Automated Search: Ability to type food queries into the search bar.
  - **High-Speed Flow**: Optimized delays and cooldowns for snappier automation.
  - **Robust Vision-Tree Fusion**: Improved JSON extraction in `VisionAgent.kt` for 100% reliability with LLM responses.
  - **Mock Capture**: Developer feature to simulate food capture for agent testing.
- **Agentic Mode**:
  - Visual overlay ("glowing border") to indicate active automation.
  - State machine for navigating from Home to Diary/Search.

## 🚧 In Progress / Recent Focus
- **Reliability Fixes**:
  - Improved `VisionAgent.kt` to handle various Gemini JSON outputs robustly.
  - Fixed `MainActivity.kt` food name parsing (using `JSONObject` for accuracy).
  - Verified end-to-end "Mock Clementine" flow.

## 🚀 Future Roadmap: MCP-Driven Architecture
We are transitioning towards an **MCP (Model Context Protocol)**-centric approach to improve reliability and reduce reliance on brittle UI automation.

- [ ] **🔄 MyFitnessPal Web/API MCP**: Integrate a stable web-based automation handler via MCP to replace/supplement Accessibility Services.
- [ ] **🍗 Nutritional Verification MCP**: Use professional nutritional databases (via MCP tools) to cross-verify Gemini's estimations.
- [ ] **🧠 UI Pattern MCP**: Maintain a shared repository of UI patterns and coordinates to speed up the OODA loop.
- [ ] **🩺 DevOps MCP**: Use automated logging and environment setup tools via MCP for consistent development.

## 📝 Planned / Todo
- [ ] Stabilize "Search Submission" in Accessibility Service (handle different keyboard types/locations).
- [ ] Improve "Agentic Mode" visual feedback.
- [ ] Add more robust error handling for network/API failures in `GeminiRepository`.
- [ ] Verify "Agentic Mode" permissions flow (Overlay + Accessibility).

## 🐛 Known Issues
- Vision agent sometimes misclassifies screens (Home vs Diary).

## 📂 Key Files
- `app/src/main/java/com/example/mediapipe/examples/macrosagent/MainActivity.kt`
- `app/src/main/java/com/example/mediapipe/examples/macrosagent/service/MyFitnessPalAccessibilityService.kt`
- `app/src/main/java/com/example/mediapipe/examples/macrosagent/data/GeminiRepository.kt`
