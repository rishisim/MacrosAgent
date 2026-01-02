package com.macros.agent.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.data.local.dao.UserMealWithItems
import com.macros.agent.data.local.entity.Food
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.ui.theme.CaloriesColor
import com.macros.agent.ui.theme.ProteinColor
import com.macros.agent.ui.theme.CarbsColor
import com.macros.agent.ui.theme.FatColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onFoodSelected: (Food) -> Unit,
    onNavigateToBarcode: () -> Unit,
    mealType: MealType = MealType.LUNCH,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Foods") },
                actions = {
                    IconButton(onClick = onNavigateToBarcode) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Scan barcode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.updateQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search USDA database...") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (val state = uiState.searchState) {
                is SearchState.Idle -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Custom Meals Section (Prominent)
                        item {
                            Text(
                                text = "My Custom Meals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        if (uiState.customMeals.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Text(
                                        text = "No custom meals yet. Save a group of items from your diary as a 'Custom Meal' to see them here!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        } else {
                            items(uiState.customMeals) { meal ->
                                CustomMealItem(
                                    mealWithItems = meal,
                                    onAddClick = { viewModel.addMealToLog(meal, mealType) }
                                )
                            }
                        }
                        
                        // Favorites
                        if (uiState.favorites.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Favorites",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(uiState.favorites.take(10)) { food ->
                                FoodSearchItem(
                                    food = food,
                                    onFoodClick = {
                                        viewModel.onFoodSelected(food)
                                        onFoodSelected(food)
                                    },
                                    onFavoriteClick = { viewModel.toggleFavorite(food) }
                                )
                            }
                        }
                        
                        // Recent Foods
                        if (uiState.recentFoods.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Recent",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(uiState.recentFoods.take(10)) { food ->
                                FoodSearchItem(
                                    food = food,
                                    onFoodClick = {
                                        viewModel.onFoodSelected(food)
                                        onFoodSelected(food)
                                    },
                                    onFavoriteClick = { viewModel.toggleFavorite(food) }
                                )
                            }
                        }
                        
                        if (uiState.favorites.isEmpty() && uiState.recentFoods.isEmpty() && uiState.customMeals.isEmpty() && uiState.query.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Search for foods or scan a barcode",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                is SearchState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is SearchState.Success -> {
                    LazyColumn {
                        items(state.foods) { food ->
                            FoodSearchItem(
                                food = food,
                                onFoodClick = {
                                    viewModel.onFoodSelected(food)
                                    onFoodSelected(food)
                                },
                                onFavoriteClick = { viewModel.toggleFavorite(food) }
                            )
                        }
                    }
                }
                
                is SearchState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodSearchItem(
    food: Food,
    onFoodClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onFoodClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!food.brandOwner.isNullOrBlank()) {
                    Text(
                        text = food.brandOwner,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${food.calories.toInt()} cal",
                        style = MaterialTheme.typography.labelMedium,
                        color = CaloriesColor
                    )
                    Text(
                        text = "P:${food.protein.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "C:${food.carbs.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "F:${food.fat.toInt()}g",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (food.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (food.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (food.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CustomMealItem(
    mealWithItems: UserMealWithItems,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mealWithItems.meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${mealWithItems.items.size} items",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "${mealWithItems.meal.totalCalories.toInt()} cal",
                        style = MaterialTheme.typography.labelMedium,
                        color = CaloriesColor
                    )
                    Text(
                        text = "P:${mealWithItems.meal.totalProtein.toInt()}g",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "C:${mealWithItems.meal.totalCarbs.toInt()}g",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "F:${mealWithItems.meal.totalFat.toInt()}g",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add meal to diary",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
