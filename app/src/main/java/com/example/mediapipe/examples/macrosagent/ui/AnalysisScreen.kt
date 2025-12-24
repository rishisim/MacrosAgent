package com.example.mediapipe.examples.macrosagent.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

// Data classes for parsed nutrition info
data class FoodItem(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)

data class NutritionAnalysis(
    val items: List<FoodItem>,
    val totalCalories: Int,
    val totalProtein: Int,
    val totalCarbs: Int,
    val totalFat: Int,
    val summary: String,
    val error: String? = null
)

fun parseAnalysisResult(json: String): NutritionAnalysis {
    return try {
        val cleanJson = json
            .replace("```json", "")
            .replace("```", "")
            .trim()
        
        val jsonObject = JSONObject(cleanJson)
        
        val items = mutableListOf<FoodItem>()
        val itemsArray = jsonObject.optJSONArray("items")
        if (itemsArray != null) {
            for (i in 0 until itemsArray.length()) {
                val item = itemsArray.getJSONObject(i)
                items.add(FoodItem(
                    name = item.optString("name", "Unknown"),
                    calories = item.optInt("calories", 0),
                    protein = item.optInt("protein_g", 0),
                    carbs = item.optInt("carbs_g", 0),
                    fat = item.optInt("fat_g", 0)
                ))
            }
        }
        
        val total = jsonObject.optJSONObject("total")
        NutritionAnalysis(
            items = items,
            totalCalories = total?.optInt("calories", 0) ?: 0,
            totalProtein = total?.optInt("protein_g", 0) ?: 0,
            totalCarbs = total?.optInt("carbs_g", 0) ?: 0,
            totalFat = total?.optInt("fat_g", 0) ?: 0,
            summary = jsonObject.optString("summary", "")
        )
    } catch (e: Exception) {
        NutritionAnalysis(
            items = emptyList(),
            totalCalories = 0,
            totalProtein = 0,
            totalCarbs = 0,
            totalFat = 0,
            summary = "",
            error = json  // Show raw response if parsing fails
        )
    }
}

@Composable
fun AnalysisScreen(
    bitmap: Bitmap?,
    analysisResult: String,
    onRetake: () -> Unit,
    onAddToMFP: () -> Unit
) {
    val analysis = remember(analysisResult) { 
        if (analysisResult.isBlank()) null else parseAnalysisResult(analysisResult) 
    }
    
    val gradientColors = listOf(
        Color(0xFF0F0F0F),
        Color(0xFF1A1A2E)
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .verticalScroll(rememberScrollState())
    ) {
        // Image Header with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured Food",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF0F0F0F))
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image Captured", color = Color.White)
                }
            }
        }

        // Loading or Content
        if (analysis == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing your meal...", color = Color.Gray)
                }
            }
        } else if (analysis.error != null && analysis.items.isEmpty()) {
            // Error or raw response
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Analysis Result",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = analysis.error,
                        fontSize = 14.sp,
                        color = Color(0xFFCCCCCC),
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            // Summary card
            if (analysis.summary.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = analysis.summary,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF81C784)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total Macros Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2D)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Total Nutrition",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Calories - Big number
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${analysis.totalCalories}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = " kcal",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Macro bars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroColumn("Protein", analysis.totalProtein, Color(0xFF64B5F6))
                        MacroColumn("Carbs", analysis.totalCarbs, Color(0xFFFFB74D))
                        MacroColumn("Fat", analysis.totalFat, Color(0xFFE57373))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Individual Items
            if (analysis.items.isNotEmpty()) {
                Text(
                    text = "Items",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                analysis.items.forEach { item ->
                    FoodItemCard(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retake", fontSize = 16.sp)
            }

            Button(
                onClick = onAddToMFP,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add to MFP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MacroColumn(label: String, grams: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${grams}g",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun FoodItemCard(item: FoodItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252538)),
        shape = RoundedCornerShape(12.dp)
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
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "P: ${item.protein}g  C: ${item.carbs}g  F: ${item.fat}g",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Text(
                text = "${item.calories}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Text(
                text = " kcal",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
