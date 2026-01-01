package com.macros.agent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.macros.agent.data.local.dao.DiaryDao
import com.macros.agent.data.local.dao.ExerciseDao
import com.macros.agent.data.local.dao.FoodDao
import com.macros.agent.data.local.dao.GeminiAnalysisDao
import com.macros.agent.data.local.dao.GoalsDao
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.ExerciseEntry
import com.macros.agent.data.local.entity.Food
import com.macros.agent.data.local.entity.GeminiAnalysis
import com.macros.agent.data.local.entity.UserGoals

@Database(
    entities = [
        Food::class,
        DiaryEntry::class,
        GeminiAnalysis::class,
        UserGoals::class,
        ExerciseEntry::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MacrosDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun diaryDao(): DiaryDao
    abstract fun goalsDao(): GoalsDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun geminiAnalysisDao(): GeminiAnalysisDao
    
    companion object {
        const val DATABASE_NAME = "macros_database"
    }
}
