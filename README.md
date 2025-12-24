# MacrosAgent

MacrosAgent is an Android application that automates nutritional tracking by analyzing food images and logging data to MyFitnessPal.

## Features

- Image-based nutritional analysis using Gemini.
- Automated food logging via Android Accessibility Services.
- Vision-based UI element detection and interaction.

## Project Structure

- `app/src/main/java/.../service/`: Contains the accessibility service and vision agent.
- `app/src/main/java/.../ui/`: Contains the Jetpack Compose UI screens.
- `app/src/main/java/.../data/`: Handles API interactions and data persistence.

## Setup

1. Clone the repository.
2. Provide a Gemini API key in `local.properties` as `GEMINI_API_KEY`.
3. Enable the MacrosAgent Accessibility Service in Android settings.
4. Build and run using Android Studio.
