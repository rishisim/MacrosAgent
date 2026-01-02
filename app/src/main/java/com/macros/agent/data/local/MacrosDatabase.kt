package com.macros.agent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.macros.agent.data.local.dao.DiaryDao
import com.macros.agent.data.local.dao.ExerciseDao
import com.macros.agent.data.local.dao.FoodDao
import com.macros.agent.data.local.dao.GeminiAnalysisDao
import com.macros.agent.data.local.dao.GoalsDao
import com.macros.agent.data.local.dao.UserMealDao
import com.macros.agent.data.local.entity.*

@Database(
    entities = [
        Food::class,
        DiaryEntry::class,
        GeminiAnalysis::class,
        UserGoals::class,
        ExerciseEntry::class,
        UserMeal::class,
        UserMealItem::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MacrosDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun diaryDao(): DiaryDao
    abstract fun goalsDao(): GoalsDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun geminiAnalysisDao(): GeminiAnalysisDao
    abstract fun userMealDao(): UserMealDao
    
    companion object {
        const val DATABASE_NAME = "macros_database"
    }
}
