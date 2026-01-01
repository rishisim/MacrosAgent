#!/bin/bash

# Package and Service Names
PACKAGE="com.example.mediapipe.examples.macrosagent"
SERVICE="com.example.mediapipe.examples.macrosagent.service.MyFitnessPalAccessibilityService"
FULL_COMPONENT="$PACKAGE/$SERVICE"

echo "🤖 Granting permissions for MacrosAgent..."

# 1. Grant SYSTEM_ALERT_WINDOW (Display Over Other Apps)
echo "1. Granting 'Display Over Other Apps'..."
adb shell appops set $PACKAGE SYSTEM_ALERT_WINDOW allow
if [ $? -eq 0 ]; then
    echo "   ✅ Success"
else
    echo "   ❌ Failed (Is device connected?)"
fi

# 2. Grant Accessibility Service Permission
echo "2. Enabling Accessibility Service..."
# Get current enabled services
CURRENT_SERVICES=$(adb shell settings get secure enabled_accessibility_services)

# Check if already enabled
if [[ "$CURRENT_SERVICES" == *"$FULL_COMPONENT"* ]]; then
    echo "   ✅ Already enabled"
else
    # Append our service to the list
    if [ "$CURRENT_SERVICES" == "null" ] || [ -z "$CURRENT_SERVICES" ]; then
        NEW_SERVICES="$FULL_COMPONENT"
    else
        NEW_SERVICES="$CURRENT_SERVICES:$FULL_COMPONENT"
    fi
    
    adb shell settings put secure enabled_accessibility_services "$NEW_SERVICES"
    adb shell settings put secure accessibility_enabled 1
    
    if [ $? -eq 0 ]; then
        echo "   ✅ Success"
    else
        echo "   ❌ Failed"
    fi
fi

echo "✨ Done! You should not need to manually enable permissions."
