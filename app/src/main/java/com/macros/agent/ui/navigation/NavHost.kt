package com.macros.agent.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.macros.agent.ui.screens.diary.DiaryScreen
import com.macros.agent.ui.screens.exercise.ExerciseScreen
import com.macros.agent.ui.screens.photo.PhotoScreen
import com.macros.agent.ui.screens.search.SearchScreen
import com.macros.agent.ui.screens.settings.SettingsScreen

@Composable
fun MacrosNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                Screen.bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { 
                        it.route == screen.route 
                    } == true
                    
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Diary.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable(Screen.Diary.route) {
                DiaryScreen(
                    onNavigateToSearch = { mealType ->
                        navController.navigate(Routes.addFood(mealType))
                    },
                    onNavigateToPhoto = {
                        navController.navigate(Screen.Photo.route)
                    }
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    onFoodSelected = { food ->
                        // Navigate back to diary with selected food
                        navController.popBackStack()
                    },
                    onNavigateToBarcode = {
                        navController.navigate(Routes.BARCODE_SCANNER)
                    }
                )
            }
            
            composable(Screen.Photo.route) {
                PhotoScreen(
                    onAnalysisComplete = { entries ->
                        // Navigate back to diary after adding entries
                        navController.navigate(Screen.Diary.route) {
                            popUpTo(Screen.Diary.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Exercise.route) {
                ExerciseScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToGoals = {
                        navController.navigate(Routes.GOALS)
                    }
                )
            }
        }
    }
}
