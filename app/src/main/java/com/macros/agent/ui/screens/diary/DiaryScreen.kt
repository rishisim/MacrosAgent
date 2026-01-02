package com.macros.agent.ui.screens.diary

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.ui.components.MacroProgressRing
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.ui.theme.CaloriesColor
import com.macros.agent.ui.theme.CarbsColor
import com.macros.agent.ui.theme.FatColor
import com.macros.agent.ui.theme.ProteinColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onNavigateToSearch: (mealType: String) -> Unit,
    onNavigateToEdit: (entryId: Long) -> Unit,
    onNavigateToPhoto: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToProgress: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.diaryState.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.goToPreviousDay() }) {
                            Icon(Icons.Default.ChevronLeft, "Previous day")
                        }
                        Text(
                            text = if (uiState.selectedDate == LocalDate.now()) {
                                "Today"
                            } else {
                                uiState.selectedDate.format(dateFormatter)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = { viewModel.goToNextDay() }) {
                            Icon(Icons.Default.ChevronRight, "Next day")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPhoto,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add food")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Daily summary card
                item {
                    DailySummaryCard(
                        uiState = uiState,
                        onNavigateToExercise = onNavigateToExercise,
                        onNavigateToProgress = onNavigateToProgress
                    )
                }
                
                // Meal sections
                val mealTypes = MealType.entries
                mealTypes.forEach { mealType ->
                    val entries = uiState.entries[mealType] ?: emptyList()
                    val mealCalories = entries.sumOf { it.calories.toDouble() }.toInt()
                    
                    // Meal Header
                    item(key = "header_${mealType.name}") {
                        MealSectionHeader(
                            title = mealType.displayName,
                            calories = if (entries.isNotEmpty()) "$mealCalories cal" else null,
                            onAddFood = { onNavigateToSearch(mealType.name) },
                            onSaveMeal = { viewModel.saveMealAsCustom(mealType, it) },
                            hasItems = entries.isNotEmpty()
                        )
                    }
                    
                    // Meal Items
                    if (entries.isEmpty()) {
                        item(key = "empty_${mealType.name}") {
                            MealEmptyState(
                                text = "Add ${mealType.displayName}",
                                onClick = { onNavigateToSearch(mealType.name) }
                            )
                        }
                    } else {
                        items(
                            items = entries,
                            key = { entry -> entry.id }
                        ) { entry ->
                            FoodEntryRow(
                                entry = entry,
                                onDelete = { viewModel.deleteEntry(entry) },
                                onClick = { onNavigateToEdit(entry.id) }
                            )
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun DailySummaryCard(
    uiState: DiaryUiState,
    onNavigateToExercise: () -> Unit,
    onNavigateToProgress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToProgress),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Remaining calories with exercise
            val remaining = uiState.remainingCalories
            val remainingColor = if (remaining >= 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
            
            Text(
                text = "$remaining",
                style = MaterialTheme.typography.displaySmall,
                color = remainingColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Remaining",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Goal - Food + Exercise = Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalorieColumn("Goal", uiState.goals.dailyCalories)
                Text("−", style = MaterialTheme.typography.titleLarge)
                CalorieColumn("Food", uiState.dailyTotals.totalCalories.toInt())
                Text("+", style = MaterialTheme.typography.titleLarge)
                CalorieColumn(
                    label = "Exercise",
                    value = uiState.exerciseCalories,
                    modifier = Modifier.clickable(onClick = onNavigateToExercise)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Macro progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroProgressRing(
                    value = uiState.dailyTotals.totalProtein,
                    goal = uiState.goals.proteinGrams.toFloat(),
                    label = "Protein",
                    color = ProteinColor
                )
                MacroProgressRing(
                    value = uiState.dailyTotals.totalCarbs,
                    goal = uiState.goals.carbsGrams.toFloat(),
                    label = "Carbs",
                    color = CarbsColor
                )
                MacroProgressRing(
                    value = uiState.dailyTotals.totalFat,
                    goal = uiState.goals.fatGrams.toFloat(),
                    label = "Fat",
                    color = FatColor
                )
            }
        }
    }
}

@Composable
private fun CalorieColumn(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



@Composable
private fun MealSectionHeader(
    title: String,
    calories: String?,
    onAddFood: () -> Unit,
    onSaveMeal: (String) -> Unit,
    hasItems: Boolean
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var mealName by remember { mutableStateOf(title) }

    if (showSaveDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save as Custom Meal") },
            text = {
                OutlinedTextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = { Text("Meal Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    onSaveMeal(mealName)
                    showSaveDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp), // Tiny padding or merge visually
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium // You might want different shapes if merging
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (calories != null) {
                    Text(
                        text = calories,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                if (hasItems) {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Save as Custom Meal",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                IconButton(onClick = onAddFood) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add to $title",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MealEmptyState(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = MaterialTheme.shapes.small
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodEntryRow(
    entry: DiaryEntry,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    // Use key to prevent unnecessary recomposition
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { it * 0.25f } // Easier to trigger
    )
    
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Card(
                modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clickable(onClick = onClick),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.foodName,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${entry.servingSize.toInt()} ${entry.servingUnit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${entry.calories.toInt()} cal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CaloriesColor
                        )
                        Text(
                            text = "P:${entry.protein.toInt()} C:${entry.carbs.toInt()} F:${entry.fat.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}
