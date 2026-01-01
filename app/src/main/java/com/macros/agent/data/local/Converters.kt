package com.macros.agent.data.local

import androidx.room.TypeConverter
import com.macros.agent.data.local.entity.ExerciseSource
import com.macros.agent.data.local.entity.MealType

/**
 * Type converters for Room database enum types.
 */
class Converters {
    
    @TypeConverter
    fun fromMealType(value: MealType): String = value.name
    
    @TypeConverter
    fun toMealType(value: String): MealType = MealType.valueOf(value)
    
    @TypeConverter
    fun fromExerciseSource(value: ExerciseSource): String = value.name
    
    @TypeConverter
    fun toExerciseSource(value: String): ExerciseSource = ExerciseSource.valueOf(value)
}
