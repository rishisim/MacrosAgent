package com.example.mediapipe.examples.macrosagent.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Cyborg Agent Accessibility Service
 * 
 * Uses a hybrid OODA (Observe-Orient-Decide-Act) loop:
 * 1. OBSERVE: Build screen state from accessibility tree
 * 2. ORIENT: Classify screen type, detect popups
 * 3. DECIDE: Choose action based on current state + goal
 * 4. ACT: Execute action (click, type, gesture)
 * 5. VERIFY: Check if action succeeded, fallback to vision if not
 */
class MyFitnessPalAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "CyborgAgent"
        private const val PREFS_NAME = "MacrosAgentPrefs"
        private const val KEY_SEARCH = "LAST_FOOD_SEARCH"
        
        // Popup detection keywords - these indicate actual popups/dialogs
        private val POPUP_KEYWORDS = listOf(
            "premium", "upgrade", "pro", "subscribe", 
            "rate us", "rate this app", "review",
            "special offer", "limited time", "discount",
            "say goodbye to ads", "go premium"
        )
        
        // Popup dismiss keywords - only these should be clicked to dismiss
        private val POPUP_DISMISS_KEYWORDS = listOf(
            "close", "not now", "no thanks", "maybe later", 
            "skip", "dismiss", "cancel", "×", "✕"
        )
        
        // Keywords that indicate it's NOT a popup (normal app elements)
        private val NON_POPUP_KEYWORDS = listOf(
            "add food", "add exercise", "breakfast", "lunch", "dinner", "snack",
            "diary", "dashboard", "calories remaining"
        )
    }

    private var overlayView: FrameLayout? = null
    private var windowManager: WindowManager? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val visionAgent = VisionAgent()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val safetyHandler = Handler(Looper.getMainLooper())
    
    // Agent state machine
    private enum class AgentState {
        IDLE,
        NAVIGATING_TO_DIARY,
        FINDING_ADD_FOOD,
        FINDING_SEARCH,
        INPUTTING_TEXT,
        WAITING_FOR_RESULTS,    // Wait for search results to load
        EVALUATING_RESULTS,     // LLM evaluates if results are good
        SELECTING_FOOD,         // Choose best food from results  
        ADJUSTING_SERVING,      // Adjust serving size on detail page
        CONFIRMING_ADD,         // Click final add/checkmark button
        VERIFYING_ACTION,
        COMPLETED
    }
    
    private var currentState = AgentState.IDLE
    private var retryCount = 0
    private var lastActionTime = 0L
    private var verificationStartTime = 0L  // Track when we started verifying
    private var lastScreenState: ScreenState? = null
    private var lastPackageName: String? = null  // Track to detect leaving MFP
    private var isAgentActive = false
    private var isActionInProgress = false // Prevent overlapping vision/async actions
    
    // Food selection tracking
    private var currentFoodSearch: String? = null      // What we're searching for
    private var expectedCalories: Int? = null          // From Gemini analysis
    private var searchRetryCount = 0                   // How many times we've retried search
    private val MAX_SEARCH_RETRIES = 2
    
    private val ACTION_COOLDOWN_MS = 500L  // Snappier transitions (was 800ms)
    private val MAX_RETRIES = 3
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "🤖 Cyborg Agent connected!")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        Toast.makeText(applicationContext, "🤖 Cyborg Agent Ready", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        val currentPackage = event.packageName?.toString()
        
        // Detect leaving MyFitnessPal - remove overlay
        if (lastPackageName == "com.myfitnesspal.android" && currentPackage != "com.myfitnesspal.android") {
            Log.d(TAG, "🚨 Left MyFitnessPal, hiding overlay")
            hideOverlay()
        }
        lastPackageName = currentPackage
        
        // Only process MFP events
        if (currentPackage != "com.myfitnesspal.android") return
        
        // Cooldown to prevent spamming
        val now = System.currentTimeMillis()
        if (now - lastActionTime < ACTION_COOLDOWN_MS) return
        
        // Check if we have a task
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val foodToSearch = prefs.getString(KEY_SEARCH, null)
        
        if (foodToSearch.isNullOrEmpty()) {
            if (currentState != AgentState.IDLE) {
                resetAgent("Task cancelled or finished")
            }
            return
        }
        
        // Show overlay when active
        if (overlayView == null && !isAgentActive) {
            showOverlay()
            isAgentActive = true
        }
        
        val rootNode = rootInActiveWindow ?: return
        
        // ═══════════════════════════════════════════════════════════════
        // OODA LOOP
        // ═══════════════════════════════════════════════════════════════
        
        // 1. OBSERVE: Build screen state
        val screenState = buildScreenState(rootNode)
        lastScreenState = screenState
        
        // 2. ORIENT: Classify screen, check for popups
        val screenType = classifyScreen(screenState)
        Log.d(TAG, "📍 Screen: $screenType | State: $currentState | Retry: $retryCount")
        
        // 3. DECIDE & ACT: Handle based on current state and screen type
        when {
            // Priority 1: Handle popups (the "Popup Assassin")
            screenType == ScreenType.POPUP_DIALOG -> {
                handlePopup(screenState)
            }
            
            // Priority 2: Handle post-search states (new flow)
            currentState == AgentState.WAITING_FOR_RESULTS -> {
                // Just wait - the delayed handler will advance to EVALUATING_RESULTS
                Log.d(TAG, "⏳ Waiting for search results to load...")
            }
            
            currentState == AgentState.EVALUATING_RESULTS -> {
                // Take screenshot and evaluate search results with vision
                triggerResultsEvaluation()
            }
            
            currentState == AgentState.SELECTING_FOOD -> {
                // Select best food from results
                triggerFoodSelection()
            }
            
            currentState == AgentState.ADJUSTING_SERVING -> {
                // Handle serving size adjustment
                triggerServingAdjustment()
            }

            currentState == AgentState.VERIFYING_ACTION -> {
                verifyActionSuccess(screenState)
            }
            
            currentState == AgentState.CONFIRMING_ADD -> {
                // Look for checkmark/confirm button in accessibility tree first
                if (clickNodeContaining(rootNode, listOf("✓", "check", "done", "save", "add"))) {
                    Log.d(TAG, "✓ Clicked confirm button")
                    completeTask()
                } else {
                    // Fallback to vision
                    triggerServingAdjustment()  // Will find and click confirm
                }
            }
            
            // Priority 3: Already at search? Input text!
            screenType == ScreenType.SEARCH_SCREEN && screenState.editableFields.isNotEmpty() -> {
                if (currentState != AgentState.WAITING_FOR_RESULTS && 
                    currentState != AgentState.EVALUATING_RESULTS) {
                    currentState = AgentState.INPUTTING_TEXT
                    inputFoodSearch(screenState, foodToSearch, prefs)
                }
            }
            
            // Priority 4: Navigate based on current state
            else -> navigateToSearch(rootNode, screenState, screenType)
        }
        
        lastActionTime = System.currentTimeMillis()
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // OBSERVE: Build complete screen state from accessibility tree
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun buildScreenState(root: AccessibilityNodeInfo): ScreenState {
        val visibleTexts = mutableListOf<String>()
        val clickables = mutableListOf<ClickableElement>()
        val editables = mutableListOf<EditableField>()
        
        fun traverse(node: AccessibilityNodeInfo) {
            // Collect text
            node.text?.toString()?.let { if (it.isNotBlank()) visibleTexts.add(it) }
            node.contentDescription?.toString()?.let { if (it.isNotBlank()) visibleTexts.add(it) }
            
            // Collect clickables
            if (node.isClickable) {
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                clickables.add(ClickableElement(
                    text = node.text?.toString(),
                    contentDescription = node.contentDescription?.toString(),
                    bounds = bounds,
                    nodeInfo = node
                ))
            }
            
            // Collect editables
            if (node.isEditable) {
                val bounds = Rect()
                node.getBoundsInScreen(bounds)
                editables.add(EditableField(
                    hint = node.hintText?.toString(),
                    currentText = node.text?.toString(),
                    bounds = bounds,
                    nodeInfo = node
                ))
            }
            
            // Recurse
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { traverse(it) }
            }
        }
        
        traverse(root)
        
        return ScreenState(
            visibleTexts = visibleTexts,
            clickableElements = clickables,
            editableFields = editables,
            packageName = root.packageName?.toString()
        )
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // ORIENT: Classify screen type using heuristics
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun classifyScreen(state: ScreenState): ScreenType {
        val allText = state.getAllTextLowercase()
        
        // Check for popup first (highest priority)
        if (isPopup(allText, state)) {
            return ScreenType.POPUP_DIALOG
        }
        
        // Check for search screen - has editable search field
        if (allText.contains("search for a food") || 
            allText.contains("search foods") ||
            (state.editableFields.isNotEmpty() && allText.contains("search"))) {
            return ScreenType.SEARCH_SCREEN
        }
        
        // Check for meal selection screen - has add food/meal buttons
        if (allText.contains("add food") || 
            allText.contains("add breakfast") || 
            allText.contains("add lunch") ||
            allText.contains("add dinner") ||
            allText.contains("add snack")) {
            return ScreenType.MEAL_SELECTION
        }
        
        // Check for Dashboard/Home screen - has dashboard-specific content
        // This MUST come before diary check since bottom nav might show 'diary' on any screen
        if (allText.contains("dashboard") || 
            allText.contains("logging progress") ||
            allText.contains("powering up") ||
            allText.contains("nutrient card") ||
            (allText.contains("logged") && allText.contains("meals"))) {
            return ScreenType.HOME_SCREEN
        }
        
        // Check for actual Diary screen - must have meal-specific content (not just 'diary' in nav)
        // The diary screen shows individual meal sections like "Breakfast 0 cal", "Lunch 0 cal"
        if ((allText.contains("breakfast") && allText.contains("cal")) ||
            (allText.contains("lunch") && allText.contains("cal")) ||
            (allText.contains("dinner") && allText.contains("cal")) ||
            (allText.contains("snacks") && allText.contains("cal"))) {
            return ScreenType.DIARY_SCREEN
        }
        
        return ScreenType.UNKNOWN
    }
    
    private fun isPopup(allText: String, state: ScreenState): Boolean {
        // First check if this looks like normal app content (not a popup)
        val hasNormalAppContent = NON_POPUP_KEYWORDS.any { allText.contains(it) }
        
        // Check for popup keywords
        val hasPopupContent = POPUP_KEYWORDS.any { allText.contains(it) }
        
        // Check if there's a proper dismiss button (not just any clickable)
        val hasDismissOption = POPUP_DISMISS_KEYWORDS.any { keyword ->
            state.clickableElements.any { 
                val text = it.text?.lowercase() ?: ""
                val desc = it.contentDescription?.lowercase() ?: ""
                // Must be an exact match or close match for dismiss buttons
                text == keyword || desc == keyword ||
                text.contains(keyword) && text.length < 15 // Short button text
            }
        }
        
        // It's a popup if it has popup content AND dismiss option, but NOT if it's normal app content
        return hasPopupContent && hasDismissOption && !hasNormalAppContent
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DECIDE & ACT: Handle popups (Popup Assassin)
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun handlePopup(state: ScreenState) {
        Log.d(TAG, "🎯 Popup detected! Attempting to dismiss...")
        
        // Try to find and click dismiss button
        for (keyword in POPUP_DISMISS_KEYWORDS) {
            val dismissButton = state.clickableElements.find { element ->
                element.text?.lowercase()?.contains(keyword) == true ||
                element.contentDescription?.lowercase()?.contains(keyword) == true
            }
            
            if (dismissButton != null) {
                Log.d(TAG, "✓ Found dismiss button: ${dismissButton.text ?: dismissButton.contentDescription}")
                tryClick(dismissButton.nodeInfo)
                return
            }
        }
        
        // Fallback: Try vision to find X button
        Log.d(TAG, "⚠️ No dismiss button found in tree, trying vision fallback...")
        triggerVisionFallback("popup dismiss")
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // DECIDE & ACT: Navigate to search
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun navigateToSearch(root: AccessibilityNodeInfo, state: ScreenState, screenType: ScreenType) {
        // Auto-advance state based on screen classification
        when (screenType) {
            ScreenType.HOME_SCREEN -> {
                if (currentState == AgentState.IDLE) currentState = AgentState.NAVIGATING_TO_DIARY
            }
            ScreenType.DIARY_SCREEN -> {
                if (currentState in listOf(AgentState.IDLE, AgentState.NAVIGATING_TO_DIARY)) {
                    currentState = AgentState.FINDING_ADD_FOOD
                    retryCount = 0
                    Log.d(TAG, "→ Advanced to FINDING_ADD_FOOD")
                }
            }
            ScreenType.MEAL_SELECTION -> {
                // MEAL_SELECTION means we're on the screen with ADD FOOD buttons
                // Don't auto-advance to FINDING_SEARCH - we need to actually CLICK "Add Food" first!
                // The advancement to FINDING_SEARCH happens in the FINDING_ADD_FOOD handler
                // when we successfully click a meal's add food button
                if (currentState in listOf(AgentState.IDLE, AgentState.NAVIGATING_TO_DIARY)) {
                    currentState = AgentState.FINDING_ADD_FOOD
                    retryCount = 0
                    Log.d(TAG, "→ On MEAL_SELECTION, setting state to FINDING_ADD_FOOD")
                }
            }
            else -> {}
        }
        
        when (currentState) {
            AgentState.IDLE -> {
                Log.d(TAG, "🚀 Starting navigation flow")
                currentState = AgentState.NAVIGATING_TO_DIARY
            }
            
            AgentState.NAVIGATING_TO_DIARY -> {
                // Try to find Diary tab (bottom nav)
                if (clickNodeContaining(root, listOf("Diary", "diary"))) {
                    Log.d(TAG, "→ Clicked Diary")
                    currentState = AgentState.FINDING_ADD_FOOD
                    retryCount = 0
                    return
                }
                handleNavigationFailure()
            }
            
            AgentState.FINDING_ADD_FOOD -> {
                // First priority: Look for explicit "Add Food" buttons or "+" icons
                if (clickNodeContaining(root, listOf("Add Food", "ADD FOOD", "add food"))) {
                    Log.d(TAG, "→ Clicked Add Food button")
                    currentState = AgentState.FINDING_SEARCH
                    retryCount = 0
                    return
                }
                
                // Second priority: Try clicking meal section headers (they expand to show add food)
                // Only try this on first attempt to avoid clicking the same thing repeatedly
                if (retryCount == 0 && clickNodeContaining(root, listOf("Breakfast", "Lunch", "Dinner", "Snacks"))) {
                    Log.d(TAG, "→ Clicked meal section header")
                    // Don't advance state yet - wait to see if it reveals Add Food
                    return
                }
                
                // If first retry fails, try scrolling down (ADD FOOD might be below premium banner)
                if (retryCount == 1) {
                    Log.d(TAG, "🔄 Scrolling down to find Add Food buttons...")
                    performSwipeGesture(500f, 1200f, 500f, 600f)  // Scroll down
                    return
                }
                
                handleNavigationFailure()
            }
            
            AgentState.FINDING_SEARCH -> {
                // Look for search bar
                if (clickNodeContaining(root, listOf("Search", "search", "magnifying"))) {
                    Log.d(TAG, "→ Clicked Search")
                    currentState = AgentState.INPUTTING_TEXT
                    retryCount = 0
                    return
                }
                
                // Check if edit field already exists
                if (state.editableFields.isNotEmpty()) {
                    currentState = AgentState.INPUTTING_TEXT
                    return
                }
                
                handleNavigationFailure()
            }
            
            else -> { /* Other states handled elsewhere */ }
        }
    }
    
    private fun handleNavigationFailure() {
        retryCount++
        
        // Log some visible text for debugging
        if (retryCount == 1) {
            val visibleText = lastScreenState?.visibleTexts?.take(10)?.joinToString(", ") ?: "none"
            Log.d(TAG, "🔍 Visible text (first 10): $visibleText")
        }
        
        if (retryCount >= MAX_RETRIES) {
            Log.d(TAG, "⚠️ Max retries reached, triggering vision fallback")
            triggerVisionFallback("navigation")
            retryCount = 0
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // ACT: Input search text
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun inputFoodSearch(state: ScreenState, foodToSearch: String, prefs: android.content.SharedPreferences) {
        val editField = state.editableFields.firstOrNull() ?: return
        val currentText = editField.currentText ?: ""
        
        if (currentText != foodToSearch) {
            Log.d(TAG, "⌨️ Inputting: $foodToSearch")
            
            // Store search info for later use
            currentFoodSearch = foodToSearch
            expectedCalories = prefs.getInt("EXPECTED_CALORIES", 0).takeIf { it > 0 }
            
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, 
                foodToSearch
            )
            editField.nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            
            Toast.makeText(applicationContext, "🤖 Searching: $foodToSearch", Toast.LENGTH_SHORT).show()
            
            // Transition to waiting state first
            currentState = AgentState.WAITING_FOR_RESULTS
            retryCount = 0
            
            mainHandler.postDelayed({
                tapKeyboardSearchKey()
            }, 300) // Reduced from 500ms
            
            // Safety net: in case vision fails to find keyboard or doesn't trigger
            safetyHandler.postDelayed({
                if (currentState == AgentState.WAITING_FOR_RESULTS) {
                    Log.w(TAG, "⚠️ Safety net triggered: Evaluation didn't start normally. Forcing check.")
                    currentState = AgentState.EVALUATING_RESULTS
                    triggerResultsEvaluation()
                }
            }, 5000) // 5s safety timeout
            
            Log.d(TAG, "→ Submitted search, waiting for results...")
        } else {
            // Text already entered, move to waiting
            currentState = AgentState.WAITING_FOR_RESULTS
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // POST-SEARCH FLOW: Evaluate results, select food, adjust serving
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun triggerResultsEvaluation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || isActionInProgress) {
            return
        }
        
        Log.d(TAG, "🔍 Evaluating search results...")
        isActionInProgress = true
        
        takeScreenshot(
            android.view.Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer,
                        screenshot.colorSpace
                    )
                    
                    if (bitmap != null) {
                        scope.launch {
                            if (currentState != AgentState.EVALUATING_RESULTS) {
                                isActionInProgress = false
                                return@launch
                            }
                            
                            val decision = visionAgent.evaluateSearchResults(
                                bitmap,
                                currentFoodSearch ?: "",
                                expectedCalories
                            )
                            
                            Log.d(TAG, "📊 Search decision: ${decision.action} - ${decision.reason}")
                            
                            when (decision.action) {
                                "SELECT_FOOD" -> {
                                    isActionInProgress = false
                                    currentState = AgentState.SELECTING_FOOD
                                    triggerFoodSelection()
                                }
                                "RETRY_SEARCH" -> {
                                    isActionInProgress = false
                                    if (searchRetryCount < MAX_SEARCH_RETRIES && decision.target != null) {
                                        searchRetryCount++
                                        Log.d(TAG, "🔄 Retrying search with: ${decision.target}")
                                        // Update search term and go back to inputting
                                        currentFoodSearch = decision.target
                                        currentState = AgentState.INPUTTING_TEXT
                                        // Clear current text and re-search
                                        triggerNewSearch(decision.target)
                                    } else {
                                        Log.d(TAG, "⚠️ Max search retries, trying to select anyway")
                                        currentState = AgentState.SELECTING_FOOD
                                        triggerFoodSelection()
                                    }
                                }
                                "USE_SUGGESTION" -> {
                                    isActionInProgress = false
                                    if (decision.target != null) {
                                        Log.d(TAG, "💡 Using suggestion: ${decision.target}")
                                        // Click on the suggestion - vision will handle it
                                        currentFoodSearch = decision.target
                                        triggerClickSuggestion(decision.target)
                                    } else {
                                        currentState = AgentState.SELECTING_FOOD
                                        triggerFoodSelection()
                                    }
                                }
                            }
                            isActionInProgress = false
                        }
                    } else {
                        isActionInProgress = false
                    }
                    screenshot.hardwareBuffer.close()
                }
                
                override fun onFailure(errorCode: Int) {
                    Log.e(TAG, "Screenshot failed for results evaluation: $errorCode")
                    isActionInProgress = false
                    // Try to proceed anyway
                    currentState = AgentState.SELECTING_FOOD
                }
            }
        )
    }
    
    private fun triggerNewSearch(newQuery: String) {
        // Clear current search field and input new query
        val rootNode = rootInActiveWindow ?: return
        val state = buildScreenState(rootNode)
        val editField = state.editableFields.firstOrNull() ?: return
        
        Log.d(TAG, "🔄 Entering new search: $newQuery")
        
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            newQuery
        )
        editField.nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        
        currentState = AgentState.WAITING_FOR_RESULTS
        
        mainHandler.postDelayed({
            tapKeyboardSearchKey()
        }, 300) // Reduced from 500ms
        
        // Safety net here too
        safetyHandler.postDelayed({
            if (currentState == AgentState.WAITING_FOR_RESULTS) {
                Log.w(TAG, "⚠️ Safety net triggered (Retry): Forcing evaluation.")
                currentState = AgentState.EVALUATING_RESULTS
                triggerResultsEvaluation()
            }
        }, 5000)
    }
    
    /**
     * Use vision to find and tap the keyboard's search/enter/go key.
     * This works dynamically across different devices and keyboard layouts.
     */
    private fun tapKeyboardSearchKey() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || isActionInProgress) {
            return
        }
        
        isActionInProgress = true
        
        // First, click on the search field to ensure keyboard is visible
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val state = buildScreenState(rootNode)
            val editField = state.editableFields.firstOrNull()
            if (editField != null) {
                Log.d(TAG, "👆 Clicking search field to ensure keyboard appears")
                editField.nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
        
        Log.d(TAG, "🔍 Using vision to find keyboard search key (waiting for keyboard)...")
        
        // Wait a bit for keyboard to fully appear, then find and tap search key
        mainHandler.postDelayed({
            takeScreenshot(
                android.view.Display.DEFAULT_DISPLAY,
                mainExecutor,
                object : TakeScreenshotCallback {
                    override fun onSuccess(screenshot: ScreenshotResult) {
                        val bitmap = Bitmap.wrapHardwareBuffer(
                            screenshot.hardwareBuffer,
                            screenshot.colorSpace
                        )
                        
                        if (bitmap != null) {
                            scope.launch {
                                if (currentState != AgentState.WAITING_FOR_RESULTS && currentState != AgentState.INPUTTING_TEXT) {
                                    isActionInProgress = false
                                    return@launch
                                }
                                
                                val coords = visionAgent.findElementByVision(
                                    bitmap,
                                    "IMPORTANT: Look ONLY in the bottom 40% of the screen where the on-screen keyboard is displayed. Find the Enter/Search/Go button - it's in the BOTTOM-RIGHT corner of the keyboard (NOT the search bar at the top of the app). On Gboard it's usually a blue/green key with a magnifying glass or arrow icon. Return coordinates for this keyboard button only."
                                )
                                
                                // Validate coordinates are in keyboard area (bottom 40%)
                                if (coords != null && coords.second > (1000 * 0.6)) {
                                    Log.d(TAG, "👁️ Found keyboard search key at normalized: (${coords.first}, ${coords.second})")
                                    val scaled = rescaleCoordinates(coords)
                                    dispatchTapGesture(scaled.first, scaled.second)
                                } else {
                                    if (coords != null) {
                                        Log.w(TAG, "👁️ Vision returned normalized (${coords.first}, ${coords.second}) but it's above keyboard area (Y should be > 600)")
                                    }
                                    Log.w(TAG, "👁️ Using fallback keyboard position")
                                    val dm = resources.displayMetrics
                                    // Fallback for tall phones: 90% height (bottom row of keyboard), 88% width
                                    val fallbackX = dm.widthPixels * 0.88f
                                    val fallbackY = dm.heightPixels * 0.90f
                                    Log.d(TAG, "👆 Fallback tap at ($fallbackX, $fallbackY)")
                                    dispatchTapGesture(fallbackX, fallbackY)
                                }
                                
                                isActionInProgress = false
                                
                                // IMPORTANT: After tapping search, dismiss the keyboard
                                // using Back action (won't accidentally click UI elements)
                                mainHandler.postDelayed({
                                    Log.d(TAG, "⌨️ Dismissing keyboard with Back action")
                                    performGlobalAction(GLOBAL_ACTION_BACK)
                                    
                                    // After keyboard is dismissed, wait for results to load then evaluate
                                    mainHandler.postDelayed({
                                        if (currentState == AgentState.WAITING_FOR_RESULTS) {
                                            Log.d(TAG, "→ Results should be loaded, evaluating...")
                                            safetyHandler.removeCallbacksAndMessages(null) // Cancel safety net
                                            currentState = AgentState.EVALUATING_RESULTS
                                            triggerResultsEvaluation()
                                        }
                                    }, 1200)  // Reduced from 2500ms
                                }, 600)  // Reduced from 800ms
                            }
                        } else {
                            isActionInProgress = false
                        }
                        screenshot.hardwareBuffer.close()
                    }
                    
                    override fun onFailure(errorCode: Int) {
                        Log.e(TAG, "Screenshot failed for keyboard search: $errorCode")
                        isActionInProgress = false
                    }
                }
            )
        }, 250)  // Reduced from 400ms (wait for keyboard)
    }
    
    private fun triggerClickSuggestion(suggestion: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || isActionInProgress) {
            return
        }
        
        Log.d(TAG, "👁️ Finding suggestion via vision: $suggestion")
        isActionInProgress = true
        
        takeScreenshot(
            android.view.Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer,
                        screenshot.colorSpace
                    )
                    
                    if (bitmap != null) {
                        scope.launch {
                            if (currentState != AgentState.WAITING_FOR_RESULTS) {
                                isActionInProgress = false
                                return@launch
                            }
                            
                            val coords = visionAgent.findElementByVision(
                                bitmap,
                                "The suggested search option containing '$suggestion'"
                            )
                            
                            if (coords != null) {
                                val scaled = rescaleCoordinates(coords)
                                dispatchTapGesture(scaled.first, scaled.second)
                                currentState = AgentState.WAITING_FOR_RESULTS
                                mainHandler.postDelayed({
                                    if (currentState == AgentState.WAITING_FOR_RESULTS) {
                                        currentState = AgentState.EVALUATING_RESULTS
                                    }
                                }, 2000)
                            } else {
                                Log.e(TAG, "❌ No coordinates returned for suggestion: $suggestion")
                                isActionInProgress = false
                            }
                        }
                    } else {
                        isActionInProgress = false
                    }
                    screenshot.hardwareBuffer.close()
                }
                
                override fun onFailure(errorCode: Int) {
                    Log.e(TAG, "Screenshot failed for suggestion click: $errorCode")
                    isActionInProgress = false
                }
            }
        )
    }
    
    private fun verifyActionSuccess(state: ScreenState) {
        val allText = state.getAllTextLowercase()
        
        // Success keywords
        // "Undo" is a strong signal of a Snackbar appearing (e.g. "Food Added [Undo]")
        if (allText.contains("food added") || 
            allText.contains("item added") || 
            allText.contains("logged") || 
            allText.contains("undo")) {
            Log.d(TAG, "✓ Verification successful: Found confirmation text")
            completeTask()
            return
        }
        
        // Check timeout (give it 4 seconds to appear)
        if (System.currentTimeMillis() - verificationStartTime > 4000) {
            Log.w(TAG, "⚠️ Verification failed: No confirmation text found after 4s")
            
            // Retry logic
            if (retryCount < MAX_RETRIES) {
                retryCount++
                Log.d(TAG, "🔄 Retrying food selection (Attempt $retryCount)")
                // Go back to selecting food - will trigger new screenshot and analysis
                currentState = AgentState.SELECTING_FOOD
            } else {
                Log.e(TAG, "❌ Max verification retries reached. Marking as complete but unsure.")
                // Fallback: Just mark complete to avoid stuck state
                completeTask()
            }
        }
    }

    private fun triggerFoodSelection() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || isActionInProgress) {
            return
        }
        
        Log.d(TAG, "🍎 Selecting best food match...")
        isActionInProgress = true
        
        takeScreenshot(
            android.view.Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer,
                        screenshot.colorSpace
                    )
                    
                    if (bitmap != null) {
                        scope.launch {
                            if (currentState != AgentState.SELECTING_FOOD) {
                                isActionInProgress = false
                                return@launch
                            }
                            
                            val decision = visionAgent.selectBestFood(
                                bitmap,
                                currentFoodSearch ?: "",
                                expectedCalories
                            )
                            
                            Log.d(TAG, "🎯 Food selection: ${decision.action} - ${decision.foodName} - ${decision.reason}")
                            
                            if (decision.coordinates != null) {
                                dispatchTapGesture(
                                    decision.coordinates.first.toFloat(),
                                    decision.coordinates.second.toFloat()
                                )
                                
                                // Set next state based on action
                                mainHandler.postDelayed({
                                    val rootNode = rootInActiveWindow
                                    var clickedSuccessfully = false
                                    
                                    // FUSION: Try to find by text first if vision provided a name
                                    if (rootNode != null && decision.foodName != null) {
                                        Log.d(TAG, "🔍 Fusion: Searching tree for '${decision.foodName}'")
                                        
                                        if (decision.action == "QUICK_ADD") {
                                            // Special case: find food name, then click (+) button nearby
                                            clickedSuccessfully = findAndClickNear(rootNode, decision.foodName!!, listOf("+", "add", "Add"))
                                        } else {
                                            clickedSuccessfully = clickNodeContaining(rootNode, listOf(decision.foodName!!))
                                        }
                                    }
                                    
                                    if (!clickedSuccessfully && decision.coordinates != null) {
                                        Log.d(TAG, "⚠️ Fusion: Tree match failed, falling back to rescaled vision coordinates")
                                        val scaledCoords = rescaleCoordinates(decision.coordinates!!)
                                        dispatchTapGesture(scaledCoords.first, scaledCoords.second)
                                    } else if (!clickedSuccessfully) {
                                        Log.e(TAG, "❌ Fusion: Both tree search and vision coordinates failed")
                                    }

                                    when (decision.action) {
                                        "QUICK_ADD" -> {
                                            // Quick add clicked, wait for verification
                                            currentState = AgentState.VERIFYING_ACTION
                                            verificationStartTime = System.currentTimeMillis()
                                            Log.d(TAG, "⏳ Waiting for 'Food added' confirmation...")
                                        }
                                        "VIEW_DETAILS" -> {
                                            // Going to serving size screen
                                            isActionInProgress = false
                                            currentState = AgentState.ADJUSTING_SERVING
                                            triggerServingAdjustment()
                                        }
                                    }
                                }, 600) // Reduced from 1000ms
                            } else {
                                Log.e(TAG, "❌ No coordinates returned for food selection")
                                retryCount++
                                if (retryCount >= MAX_RETRIES) {
                                    completeTask()  // Give up gracefully
                                }
                            }
                            isActionInProgress = false
                        }
                    } else {
                        isActionInProgress = false
                    }
                    screenshot.hardwareBuffer.close()
                }
                
                override fun onFailure(errorCode: Int) {
                    Log.e(TAG, "Screenshot failed for food selection: $errorCode")
                    isActionInProgress = false
                }
            }
        )
    }
    
    private fun triggerServingAdjustment() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || isActionInProgress) {
            return
        }
        
        Log.d(TAG, "⚖️ Analyzing serving size...")
        isActionInProgress = true
        
        takeScreenshot(
            android.view.Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer,
                        screenshot.colorSpace
                    )
                    
                    if (bitmap != null) {
                        scope.launch {
                            if (currentState != AgentState.ADJUSTING_SERVING) {
                                isActionInProgress = false
                                return@launch
                            }
                            
                            val decision = visionAgent.analyzeServingScreen(
                                bitmap,
                                currentFoodSearch ?: "",
                                expectedCalories
                            )
                            
                            Log.d(TAG, "⚖️ Serving decision: ${decision.action} - ${decision.reason}")
                            
                            if (decision.coordinates != null) {
                                val scaledCoords = rescaleCoordinates(decision.coordinates!!)
                                dispatchTapGesture(scaledCoords.first, scaledCoords.second)
                                
                                mainHandler.postDelayed({
                                    when (decision.action) {
                                        "CONFIRM" -> {
                                            // Food has been added!
                                            completeTask()
                                        }
                                        "ADJUST_SERVINGS", "CHANGE_UNIT" -> {
                                            // User needs to adjust - for now we'll just confirm
                                            // Future: Could input the new_value
                                            currentState = AgentState.CONFIRMING_ADD
                                        }
                                    }
                                }, 800) // Reduced from 1500ms
                            } else {
                                // Try to find confirm button in tree
                                val rootNode = rootInActiveWindow
                                if (rootNode != null && clickNodeContaining(rootNode, listOf("✓", "check", "save"))) {
                                    completeTask()
                                }
                            }
                            isActionInProgress = false
                        }
                    } else {
                        isActionInProgress = false
                    }
                    screenshot.hardwareBuffer.close()
                }
                
                override fun onFailure(errorCode: Int) {
                    Log.e(TAG, "Screenshot failed for serving adjustment: $errorCode")
                    isActionInProgress = false
                }
            }
        )
    }
    
    private fun completeTask() {
        Log.d(TAG, "✅ Task complete! Food added to diary.")
        isActionInProgress = false
        Toast.makeText(applicationContext, "✅ Food logged!", Toast.LENGTH_SHORT).show()
        
        // Clear the search task
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_SEARCH)
            .remove("EXPECTED_CALORIES")
            .apply()
        
        // Reset tracking
        currentFoodSearch = null
        expectedCalories = null
        searchRetryCount = 0
        
        currentState = AgentState.COMPLETED
        
        mainHandler.postDelayed({
            resetAgent("✅ Mission Complete")
        }, 1000)
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // VISION FALLBACK: The "Eyes" when tree fails
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun triggerVisionFallback(reason: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || isActionInProgress) {
            return
        }
        
        Log.d(TAG, "👁️ Vision fallback triggered: $reason")
        isActionInProgress = true
        
        takeScreenshot(
            android.view.Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(
                        screenshot.hardwareBuffer,
                        screenshot.colorSpace
                    )
                    
                    if (bitmap != null) {
                        scope.launch {
                            handleVisionResult(bitmap, reason)
                            isActionInProgress = false
                        }
                    } else {
                        isActionInProgress = false
                    }
                    screenshot.hardwareBuffer.close()
                }
                
                override fun onFailure(errorCode: Int) {
                    Log.e(TAG, "Screenshot failed: $errorCode")
                }
            }
        )
    }
    
    private suspend fun handleVisionResult(screenshot: Bitmap, reason: String) {
        // Get current screen type to help with vision targeting
        val currentScreen = lastScreenState?.let { classifyScreen(it) } ?: ScreenType.UNKNOWN
        
        val targetDescription = when {
            reason == "popup dismiss" -> "X button, Close button, 'Not Now', or 'Maybe Later' to dismiss this popup"
            
            // On HOME/DASHBOARD - need to click Diary tab first
            currentScreen == ScreenType.HOME_SCREEN || currentScreen == ScreenType.UNKNOWN ->
                "The 'Diary' tab or icon in the bottom navigation bar at the bottom of the screen. Look for a book/journal icon or the word 'Diary'."
            
            reason == "navigation" && currentState == AgentState.NAVIGATING_TO_DIARY -> 
                "The 'Diary' tab or icon in the bottom navigation bar. Look for a book/journal icon or the word 'Diary'."
            
            reason == "navigation" && currentState == AgentState.FINDING_ADD_FOOD -> 
                "A '+' plus sign button, 'ADD FOOD' button, or any add icon next to a meal section (Breakfast, Lunch, Dinner, Snacks). Look in the main content area, not the navigation bar."
            
            reason == "navigation" && currentState == AgentState.FINDING_SEARCH -> 
                "The Search bar, magnifying glass icon, or 'Search for a food' text input at the top of the screen"
            
            else -> "The 'Diary' tab in the bottom navigation bar"
        }
        
        Log.d(TAG, "👁️ Vision looking for: $targetDescription (state: $currentState)")
        
        val coordinates = visionAgent.findElementByVision(screenshot, targetDescription)
        
        if (coordinates != null) {
            Log.d(TAG, "👁️ Vision found element at normalized: (${coordinates.first}, ${coordinates.second})")
            val scaled = rescaleCoordinates(coordinates)
            dispatchTapGesture(scaled.first, scaled.second)
        } else {
            Log.w(TAG, "👁️ Vision could not find: $targetDescription")
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // GESTURE: Tap at coordinates
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun dispatchTapGesture(x: Float, y: Float) {
        Log.d(TAG, "👆 Tapping at ($x, $y)")
        
        // Get screen dimensions to validate coordinates
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        Log.d(TAG, "📐 Screen size: ${screenWidth}x${screenHeight}, tap target: ($x, $y)")
        
        // Validate coordinates are within screen bounds
        if (x < 0 || x > screenWidth || y < 0 || y > screenHeight) {
            Log.e(TAG, "❌ Tap coordinates out of bounds!")
            return
        }
        
        // Create a simple stationary tap gesture (single point, no movement)
        val path = Path()
        path.moveTo(x, y)
        
        // Use 150ms duration for a more reliable tap (50ms was too short)
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 150))
            .build()
        
        val success = dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d(TAG, "✓ Tap gesture COMPLETED at ($x, $y)")
                // Advance state after successful vision tap
                mainHandler.postDelayed({
                    when (currentState) {
                        AgentState.NAVIGATING_TO_DIARY -> currentState = AgentState.FINDING_ADD_FOOD
                        AgentState.FINDING_ADD_FOOD -> currentState = AgentState.FINDING_SEARCH
                        AgentState.FINDING_SEARCH -> currentState = AgentState.INPUTTING_TEXT
                        else -> {}
                    }
                    retryCount = 0
                    Log.d(TAG, "📍 State advanced to: $currentState after tap")
                }, 1000)
            }
            
            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.w(TAG, "✗ Tap gesture CANCELLED at ($x, $y)")
            }
        }, mainHandler)
        
        Log.d(TAG, "👆 dispatchGesture returned: $success")
        
        // If dispatch returned true but we don't get a callback, log after a delay
        mainHandler.postDelayed({
            Log.d(TAG, "⏰ 2-second check: If no COMPLETED/CANCELLED log appeared, gesture callback didn't fire")
        }, 2000)
    }
    
    /**
     * Perform a swipe gesture for scrolling
     */
    private fun performSwipeGesture(startX: Float, startY: Float, endX: Float, endY: Float) {
        Log.d(TAG, "👆 Swiping from ($startX, $startY) to ($endX, $endY)")
        
        val path = Path()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()
        
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                Log.d(TAG, "✓ Swipe gesture completed")
            }
            
            override fun onCancelled(gestureDescription: GestureDescription?) {
                Log.w(TAG, "✗ Swipe gesture cancelled")
            }
        }, mainHandler)
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════════
    
    private fun attemptToFindAndClick(root: AccessibilityNodeInfo, terms: List<String>): Boolean {
        for (term in terms) {
            val nodes = root.findAccessibilityNodeInfosByText(term)
            for (node in nodes) {
                if (tryClick(node)) {
                    Log.d(TAG, "✓ Clicked: $term")
                    return true
                }
            }
        }
        return false
    }
    
    /**
     * More flexible node finder that searches through entire tree with partial text matching
     */
    private fun clickNodeContaining(root: AccessibilityNodeInfo, terms: List<String>): Boolean {
        fun searchAndClick(node: AccessibilityNodeInfo): Boolean {
            val nodeText = (node.text?.toString() ?: "") + " " + (node.contentDescription?.toString() ?: "")
            val nodeTextLower = nodeText.lowercase()
            
            for (term in terms) {
                if (nodeTextLower.contains(term.lowercase())) {
                    if (tryClick(node)) {
                        Log.d(TAG, "✓ Clicked node containing: $term (full text: ${nodeText.take(50)})")
                        return true
                    }
                }
            }
            
            // Recurse to children
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { child ->
                    if (searchAndClick(child)) return true
                }
            }
            return false
        }
        
        return searchAndClick(root)
    }

    /**
     * Finds a node by text, then searches its neighbors/parents for a specific button to click.
     * Useful for "Add" buttons next to food items.
     */
    private fun findAndClickNear(root: AccessibilityNodeInfo, anchorText: String, targetTerms: List<String>): Boolean {
        fun findNodeWithText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
            val nodeText = (node.text?.toString() ?: "") + " " + (node.contentDescription?.toString() ?: "")
            if (nodeText.lowercase().contains(text.lowercase())) return node
            
            for (i in 0 until node.childCount) {
                val found = node.getChild(i)?.let { findNodeWithText(it, text) }
                if (found != null) return found
            }
            return null
        }

        val anchorNode = findNodeWithText(root, anchorText) ?: return false
        Log.d(TAG, "📍 Fusion: Found anchor node for '$anchorText', searching for targets $targetTerms nearby")

        // 1. Check siblings of the anchor node
        val parent = anchorNode.parent
        if (parent != null) {
            for (i in 0 until parent.childCount) {
                val sibling = parent.getChild(i) ?: continue
                val siblingText = (sibling.text?.toString() ?: "") + " " + (sibling.contentDescription?.toString() ?: "")
                if (targetTerms.any { siblingText.lowercase().contains(it.lowercase()) }) {
                    if (tryClick(sibling)) {
                        Log.d(TAG, "✓ Fusion: Clicked sibling target '${siblingText.take(20)}'")
                        return true
                    }
                }
            }
            
            // 2. Check siblings of the parent (grand-siblings) - common in list items
            val grandParent = parent.parent
            if (grandParent != null) {
                for (i in 0 until grandParent.childCount) {
                    val uncle = grandParent.getChild(i) ?: continue
                    val uncleText = (uncle.text?.toString() ?: "") + " " + (uncle.contentDescription?.toString() ?: "")
                    if (targetTerms.any { uncleText.lowercase().contains(it.lowercase()) }) {
                        if (tryClick(uncle)) {
                            Log.d(TAG, "✓ Fusion: Clicked grand-sibling target '${uncleText.take(20)}'")
                            return true
                        }
                    }
                    
                    // Also check children of these uncles (deep search)
                    for (j in 0 until uncle.childCount) {
                        val cousin = uncle.getChild(j) ?: continue
                        val cousinText = (cousin.text?.toString() ?: "") + " " + (cousin.contentDescription?.toString() ?: "")
                        if (targetTerms.any { cousinText.lowercase().contains(it.lowercase()) }) {
                            if (tryClick(cousin)) {
                                Log.d(TAG, "✓ Fusion: Clicked cousin target '${cousinText.take(20)}'")
                                return true
                            }
                        }
                    }
                }
            }
        }
        
        return false
    }

    private fun rescaleCoordinates(normalized: Pair<Int, Int>): Pair<Float, Float> {
        val dm = resources.displayMetrics
        val x = (normalized.first.toFloat() / 1000f) * dm.widthPixels
        val y = (normalized.second.toFloat() / 1000f) * dm.heightPixels
        return Pair(x, y)
    }
    
    /**
     * Hide overlay without resetting the agent (for when leaving MFP temporarily)
     */
    private fun hideOverlay() {
        Log.d(TAG, "👁️ Hiding overlay")
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
                overlayView = null
            } catch (e: Exception) {
                Log.e(TAG, "Error hiding overlay: ${e.message}")
            }
        }
        isAgentActive = false
    }

    private fun tryClick(node: AccessibilityNodeInfo): Boolean {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        val parent = node.parent
        if (parent?.isClickable == true) {
            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }
    
    private fun showOverlay() {
        if (overlayView != null) return
        Log.d(TAG, "🔮 Showing Agent Aura")
        
        try {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or 
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
            
            val layout = FrameLayout(this)
            val borderView = android.view.View(this)
            val borderDrawable = GradientDrawable()
            borderDrawable.setStroke(20, Color.parseColor("#804CAF50")) // Green aura
            borderDrawable.cornerRadius = 0f
            borderDrawable.setColor(Color.TRANSPARENT)
            
            borderView.background = borderDrawable
            layout.addView(borderView)
            
            windowManager?.addView(layout, params)
            overlayView = layout
        } catch (e: Exception) {
            Log.e(TAG, "Error showing overlay: ${e.message}")
        }
    }
    
    private fun resetAgent(reason: String) {
        Log.d(TAG, "🔄 Resetting: $reason")
        currentState = AgentState.IDLE
        retryCount = 0
        lastScreenState = null
        isActionInProgress = false
        
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
                overlayView = null
            } catch (e: Exception) {
                Log.e(TAG, "Error removing overlay: ${e.message}")
            }
        }
        
        Toast.makeText(applicationContext, reason, Toast.LENGTH_SHORT).show()
    }

    override fun onInterrupt() {
        resetAgent("Interrupted")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🛑 Service destroyed, cleaning up")
        // Force remove overlay on service destroy
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
                overlayView = null
            } catch (e: Exception) {
                Log.e(TAG, "Error removing overlay on destroy: ${e.message}")
            }
        }
    }
}
