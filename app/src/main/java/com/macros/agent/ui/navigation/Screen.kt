package com.macros.agent.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes for the app.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Diary : Screen(
        route = "diary",
        title = "Diary",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    )
    
    data object Search : Screen(
        route = "search",
        title = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )
    
    data object Photo : Screen(
        route = "photo",
        title = "Scan",
        selectedIcon = Icons.Filled.CameraAlt,
        unselectedIcon = Icons.Outlined.CameraAlt
    )
    
    data object Exercise : Screen(
        route = "exercise",
        title = "Exercise",
        selectedIcon = Icons.Filled.FitnessCenter,
        unselectedIcon = Icons.Outlined.FitnessCenter
    )
    
    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
    
    companion object {
        val bottomNavItems = listOf(Diary, Search, Photo, Exercise, Settings)
    }
}

/**
 * Routes for nested navigation (not in bottom nav).
 */
object Routes {
    const val FOOD_DETAIL = "food/{fdcId}?mealType={mealType}&date={date}"
    const val ADD_FOOD = "add_food?mealType={mealType}&date={date}"
    const val EDIT_ENTRY = "edit_entry/{entryId}"
    const val BARCODE_SCANNER = "barcode_scanner"
    const val GOALS = "goals"
    const val PROGRESS_CHART = "progress_chart"
    
    fun foodDetail(fdcId: Int, mealType: String? = null, date: String? = null) = 
        "food/$fdcId?mealType=${mealType ?: ""}&date=${date ?: ""}"
    fun addFood(mealType: String, date: String) = "add_food?mealType=$mealType&date=$date"
    fun editEntry(entryId: Long) = "edit_entry/$entryId"
    
    const val EXERCISE = "exercise"
}
