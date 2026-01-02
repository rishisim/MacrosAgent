package com.macros.agent.ui.screens.food

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.data.local.entity.MealType
import com.macros.agent.ui.theme.CaloriesColor
import com.macros.agent.ui.theme.CarbsColor
import com.macros.agent.ui.theme.FatColor
import com.macros.agent.ui.theme.ProteinColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: FoodDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isSaved) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Food" else "Add Food") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.isEditMode) {
                        var showDeleteConfirm by remember { mutableStateOf(false) }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                        
                        if (showDeleteConfirm) {
                            AlertDialog(
                                onDismissRequest = { showDeleteConfirm = false },
                                title = { Text("Delete Entry") },
                                text = { Text("Are you sure you want to remove this from your diary?") },
                                confirmButton = {
                                    androidx.compose.material3.TextButton(
                                        onClick = { 
                                            viewModel.deleteEntry()
                                            showDeleteConfirm = false
                                        }
                                    ) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    androidx.compose.material3.TextButton(onClick = { showDeleteConfirm = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading || uiState.food == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val food = uiState.food!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (food.brandName != null || food.brandOwner != null) {
                    Text(
                        text = food.brandName ?: food.brandOwner ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Macro Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MacroItem(
                            value = uiState.totalCalories,
                            unit = "",
                            label = "Calories",
                            color = CaloriesColor
                        )
                        MacroItem(
                            value = uiState.totalProtein,
                            unit = "g",
                            label = "Protein",
                            color = ProteinColor
                        )
                        MacroItem(
                            value = uiState.totalCarbs,
                            unit = "g",
                            label = "Carbs",
                            color = CarbsColor
                        )
                        MacroItem(
                            value = uiState.totalFat,
                            unit = "g",
                            label = "Fat",
                            color = FatColor
                        )
                    }
                }

                // Quantity / Servings
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quantity",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.incrementQuantity(-0.5f) }
                            ) {
                                Icon(Icons.Default.Remove, "Decrease")
                            }
                            
                            OutlinedTextField(
                                value = uiState.servingQuantityString,
                                onValueChange = { viewModel.updateQuantity(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.width(80.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center),
                                singleLine = true
                            )
                            
                            IconButton(onClick = { viewModel.incrementQuantity(0.5f) }) {
                                Icon(Icons.Default.Add, "Increase")
                            }
                        }
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unit",
                            style = MaterialTheme.typography.labelMedium
                        )
                        var expanded by remember { mutableStateOf(false) }
                        val units = listOf("g", "oz", "lbs", "cup", "ml", "tbsp", "tsp", "piece")
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = uiState.servingUnit,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                units.forEach { unit ->
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            viewModel.updateServingUnit(unit)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Adjustment
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, "AI", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Portion Adjuster",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        var aiRequest by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = aiRequest,
                            onValueChange = { aiRequest = it },
                            placeholder = { Text("e.g., '2.5 pieces', 'half a pizza'", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                if (uiState.isAnalyzing) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                } else {
                                    IconButton(
                                        onClick = { 
                                            if (aiRequest.isNotBlank()) {
                                                viewModel.adjustWithAI(aiRequest)
                                            }
                                        },
                                        enabled = aiRequest.isNotBlank()
                                    ) {
                                        Icon(Icons.Default.Check, "Apply")
                                    }
                                }
                            }
                        )
                        Text(
                            text = "Describe the portion and Gemini will calculate the new macros.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "Meal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MealType.entries.forEach { mealType ->
                        val isSelected = uiState.selectedMealType == mealType
                        Surface(
                            onClick = { viewModel.updateMealType(mealType) },
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clip(CircleShape)
                        ) {
                            Text(
                                text = mealType.displayName.substring(0, 1),
                                modifier = Modifier.padding(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                Text(
                    text = uiState.selectedMealType.displayName,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { viewModel.addToDiary() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(if (uiState.isEditMode) Icons.Default.Check else Icons.Default.Add, null)
                    Text(
                        text = if (uiState.isEditMode) "Update Entry" else "Add to Diary",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MacroItem(
    value: Float,
    unit: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${value.toInt()}$unit",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
