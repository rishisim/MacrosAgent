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
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.ui.screens.diary.DiaryScreen
import com.macros.agent.ui.screens.diary.DiaryViewModel
import com.macros.agent.ui.screens.exercise.ExerciseScreen
import com.macros.agent.ui.screens.photo.PhotoScreen
import com.macros.agent.ui.screens.search.SearchScreen
import com.macros.agent.ui.screens.search.SearchViewModel
import com.macros.agent.ui.screens.search.BarcodeScannerScreen
import com.macros.agent.ui.screens.diary.ProgressChartScreen
import com.macros.agent.ui.screens.settings.GoalsScreen
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
                    val currentRoute = currentDestination?.route
                    val isSubRoute = when (screen) {
                        Screen.Diary -> currentRoute?.startsWith("add_food") == true ||
                                       currentRoute?.startsWith("food") == true ||
                                       currentRoute?.startsWith("edit_entry") == true ||
                                       currentRoute == Routes.PROGRESS_CHART
                        Screen.Settings -> currentRoute == Routes.GOALS
                        Screen.Search -> currentRoute == Routes.BARCODE_SCANNER
                        else -> false
                    }
                    
                    val selected = currentDestination?.hierarchy?.any { 
                        it.route == screen.route 
                    } == true || isSubRoute
                    
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
                            if (selected) {
                                // If already in this tab's hierarchy, pop back to the root of the tab
                                val popped = navController.popBackStack(screen.route, inclusive = false)
                                if (!popped && currentDestination?.route != screen.route) {
                                    // If pop failed and we're not already at the root, navigate to the root
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            } else {
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
                val diaryViewModel = hiltViewModel<DiaryViewModel>()
                DiaryScreen(
                    onNavigateToSearch = { mealType ->
                        val dateString = diaryViewModel.diaryState.value.selectedDate.toString()
                        navController.navigate(Routes.addFood(mealType, dateString))
                    },
                    onNavigateToEdit = { entryId ->
                        navController.navigate(Routes.editEntry(entryId))
                    },
                    onNavigateToPhoto = {
                        navController.navigate(Screen.Photo.route)
                    },
                    onNavigateToExercise = {
                        navController.navigate(Screen.Exercise.route)
                    },
                    onNavigateToProgress = {
                        navController.navigate(Routes.PROGRESS_CHART)
                    }
                )
            }
            
            composable(Screen.Search.route) { backStackEntry ->
                val viewModel = hiltViewModel<SearchViewModel>()
                
                val scannedBarcode = backStackEntry.savedStateHandle.get<String>("scanned_barcode")
                androidx.compose.runtime.LaunchedEffect(scannedBarcode) {
                    if (scannedBarcode != null) {
                        viewModel.searchByBarcode(scannedBarcode)
                        backStackEntry.savedStateHandle.remove<String>("scanned_barcode")
                    }
                }
                
                SearchScreen(
                    onFoodSelected = { food ->
                        navController.navigate(Routes.foodDetail(food.fdcId, null))
                    },
                    onNavigateToBarcode = {
                        navController.navigate(Routes.BARCODE_SCANNER)
                    },
                    mealType = MealType.LUNCH,
                    viewModel = viewModel
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
                ExerciseScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToGoals = {
                        navController.navigate(Routes.GOALS)
                    }
                )
            }
            
            composable(Routes.GOALS) {
                GoalsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            


            composable(
                route = Routes.ADD_FOOD,
                arguments = listOf(
                    androidx.navigation.navArgument("mealType") { 
                        type = androidx.navigation.NavType.StringType 
                        nullable = true
                    },
                    androidx.navigation.navArgument("date") {
                        type = androidx.navigation.NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val viewModel = hiltViewModel<SearchViewModel>()
                val mealType = backStackEntry.arguments?.getString("mealType")
                val date = backStackEntry.arguments?.getString("date")
                
                val scannedBarcode = backStackEntry.savedStateHandle.get<String>("scanned_barcode")
                androidx.compose.runtime.LaunchedEffect(scannedBarcode) {
                    if (scannedBarcode != null) {
                        viewModel.searchByBarcode(scannedBarcode)
                        backStackEntry.savedStateHandle.remove<String>("scanned_barcode")
                    }
                }
                
                SearchScreen(
                    onFoodSelected = { food ->
                        navController.navigate(Routes.foodDetail(food.fdcId, mealType, date))
                    },
                    onNavigateToBarcode = {
                        navController.navigate(Routes.BARCODE_SCANNER)
                    },
                    mealType = mealType?.let { MealType.valueOf(it) } ?: MealType.LUNCH,
                    viewModel = viewModel
                )
            }

            composable(
                route = Routes.FOOD_DETAIL,
                arguments = listOf(
                    androidx.navigation.navArgument("fdcId") { type = androidx.navigation.NavType.IntType },
                    androidx.navigation.navArgument("mealType") { 
                        type = androidx.navigation.NavType.StringType
                        nullable = true
                    },
                    androidx.navigation.navArgument("date") {
                        type = androidx.navigation.NavType.StringType
                        nullable = true
                    }
                )
            ) {
                com.macros.agent.ui.screens.food.FoodDetailScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.EDIT_ENTRY,
                arguments = listOf(
                    androidx.navigation.navArgument("entryId") { type = androidx.navigation.NavType.LongType }
                )
            ) {
                com.macros.agent.ui.screens.food.FoodDetailScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Routes.BARCODE_SCANNER) {
                BarcodeScannerScreen(
                    onBarcodeScanned = { barcode ->
                        // Pass result back to search screen
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("scanned_barcode", barcode)
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.PROGRESS_CHART) {
                ProgressChartScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
