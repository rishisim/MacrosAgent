package com.macros.agent.ui.screens.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.macros.agent.ui.theme.CaloriesColor
import com.macros.agent.ui.theme.CarbsColor
import com.macros.agent.ui.theme.FatColor
import com.macros.agent.ui.theme.ProteinColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateBack: () -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Goal Calculator Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Goal Calculator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    
                    Text(
                        text = "Calculate optimal calories and macros based on your profile.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Gender and Age
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Gender", style = MaterialTheme.typography.labelSmall)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                listOf("Male", "Female").forEach { gender ->
                                    val selected = uiState.goals.gender == gender
                                    OutlinedButton(
                                        onClick = { viewModel.updateGender(gender) },
                                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                                        shape = MaterialTheme.shapes.small,
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                        colors = if (selected) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.outlinedButtonColors()
                                    ) {
                                        Text(gender, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                        OutlinedTextField(
                            value = uiState.goals.age.toString(),
                            onValueChange = { it.toIntOrNull()?.let { v -> viewModel.updateAge(v) } },
                            modifier = Modifier.weight(0.5f),
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    
                    // Height and Units
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Bottom) {
                        OutlinedTextField(
                            value = uiState.goals.height.toString(),
                            onValueChange = { it.toFloatOrNull()?.let { v -> viewModel.updateHeight(v) } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Height") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        Row(modifier = Modifier.weight(1f)) {
                            listOf("cm", "in").forEach { unit ->
                                val selected = uiState.goals.heightUnit == unit
                                OutlinedButton(
                                    onClick = { viewModel.updateHeightUnit(unit) },
                                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                                    shape = MaterialTheme.shapes.small,
                                    contentPadding = PaddingValues(0.dp),
                                    colors = if (selected) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Text(unit, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    
                    // Current Weight and Units
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Bottom) {
                        OutlinedTextField(
                            value = uiState.goals.currentWeight.toString(),
                            onValueChange = { it.toFloatOrNull()?.let { v -> viewModel.updateCurrentWeight(v) } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Current Weight") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        Row(modifier = Modifier.weight(1f)) {
                            listOf("kg", "lbs").forEach { unit ->
                                val selected = uiState.goals.weightUnit == unit
                                OutlinedButton(
                                    onClick = { viewModel.updateWeightUnit(unit) },
                                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                                    shape = MaterialTheme.shapes.small,
                                    contentPadding = PaddingValues(0.dp),
                                    colors = if (selected) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Text(unit, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    
                    // Target Weight and Gain Rate
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = uiState.goals.targetWeight.toString(),
                            onValueChange = { it.toFloatOrNull()?.let { v -> viewModel.updateTargetWeight(v) } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Target Weight") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.goals.weightGainRate.toString(),
                            onValueChange = { it.toFloatOrNull()?.let { v -> viewModel.updateWeightGainRate(v) } },
                            modifier = Modifier.weight(1f),
                            label = { Text("Gain Rate / week") },
                            suffix = { Text(uiState.goals.weightUnit) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }
                    
                    // Activity Level
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Activity Level", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Sedentary", "Light", "Moderate", "Very", "Super").forEach { level ->
                                val selected = uiState.goals.activityLevel == level
                                OutlinedButton(
                                    onClick = { viewModel.updateActivityLevel(level) },
                                    shape = MaterialTheme.shapes.small,
                                    colors = if (selected) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Text(level, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.calculateAndApplyGoals() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Calculate & Apply")
                    }
                }
            }

            // Calories
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Daily Calories",
                        style = MaterialTheme.typography.titleMedium,
                        color = CaloriesColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.goals.dailyCalories.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { v -> viewModel.updateCalories(v) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Calories") },
                        suffix = { Text("cal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
            
            // Auto-calculate button
            OutlinedButton(
                onClick = { viewModel.autoCalculateMacros() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Text("  Auto-calculate Macros (30/40/30)")
            }
            
            // Macros
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Macronutrients",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Protein
                    MacroGoalInput(
                        label = "Protein",
                        value = uiState.goals.proteinGrams,
                        onValueChange = { viewModel.updateProtein(it) },
                        color = ProteinColor
                    )
                    
                    // Carbs
                    MacroGoalInput(
                        label = "Carbohydrates",
                        value = uiState.goals.carbsGrams,
                        onValueChange = { viewModel.updateCarbs(it) },
                        color = CarbsColor
                    )
                    
                    // Fat
                    MacroGoalInput(
                        label = "Fat",
                        value = uiState.goals.fatGrams,
                        onValueChange = { viewModel.updateFat(it) },
                        color = FatColor
                    )
                }
            }
            
            // Additional goals
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Additional Goals",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.goals.fiberGrams.toString(),
                            onValueChange = { 
                                it.toIntOrNull()?.let { v -> viewModel.updateFiber(v) }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Fiber") },
                            suffix = { Text("g") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = uiState.goals.sodiumMg.toString(),
                            onValueChange = { 
                                it.toIntOrNull()?.let { v -> viewModel.updateSodium(v) }
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Sodium") },
                            suffix = { Text("mg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Save button
            Button(
                onClick = { viewModel.saveGoals() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Goals")
            }
            
            if (uiState.saveSuccess) {
                Text(
                    text = "✓ Goals saved!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MacroGoalInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { it.toIntOrNull()?.let { v -> onValueChange(v) } },
            modifier = Modifier.weight(1f),
            suffix = { Text("g") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}
