# Gemini Agent Instructions

## 🎯 Goal
This file serves as a persistent instruction set for any AI agent working on this codebase. The goal is to keep `PROJECT_STATUS.md` acting as a "living document" that accurately reflects the state of the project.

## 📋 Rules for Updating `PROJECT_STATUS.md`

### 1. When to Update
- **After implementing a feature**: Move the item from "Planned" to "Current Features".
- **When starting a complex task**: Add it to "In Progress".
- **When discovering a bug**: Add it to "Known Issues".
- **When fixing a bug**: Remove it from "Known Issues" or move it to a "Resolved" list (deployment notes).
- **At the start of a session**: Read `PROJECT_STATUS.md` to understand context.
- **At the end of a session**: Update the file to reflect what was accomplished.

### 2. How to Update
- **Be Concise**: specific bullet points are better than long paragraphs.
- **Link Code**: Mention relevant file names when describing features or bugs (e.g., "Fixed login in `AuthActivity.kt`").
- **Keep it Clean**: If "In Progress" items are done, move them to "Current Features" and remove them from "In Progress".

## 🧠 Context Awareness
- Always check `PROJECT_STATUS.md` before answering "What is the status?" or "What should I work on next?".
- Use this file to ground your understanding of the architecture (refer to "Key Files").

## 🖼️ Asset Management
- **Diagnostic Screenshots**: All screenshots and UI dumps (`.png`, `.xml`) used for debugging or vision analysis MUST be saved in the `debug_screenshots/` directory.
- **Cleanup**: Delete all diagnostic files from `debug_screenshots/` at the end of a session unless they are explicitly requested for preservation.
- **Git Hygiene**: Never push diagnostic assets to the remote repository. Ensure `debug_screenshots/` is always in `.gitignore`.
