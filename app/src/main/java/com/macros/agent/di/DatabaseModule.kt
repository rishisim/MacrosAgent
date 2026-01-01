package com.macros.agent.di

import android.content.Context
import androidx.room.Room
import com.macros.agent.data.local.MacrosDatabase
import com.macros.agent.data.local.dao.DiaryDao
import com.macros.agent.data.local.dao.ExerciseDao
import com.macros.agent.data.local.dao.FoodDao
import com.macros.agent.data.local.dao.GeminiAnalysisDao
import com.macros.agent.data.local.dao.GoalsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MacrosDatabase {
        return Room.databaseBuilder(
            context,
            MacrosDatabase::class.java,
            MacrosDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideFoodDao(database: MacrosDatabase): FoodDao = database.foodDao()
    
    @Provides
    fun provideDiaryDao(database: MacrosDatabase): DiaryDao = database.diaryDao()
    
    @Provides
    fun provideGoalsDao(database: MacrosDatabase): GoalsDao = database.goalsDao()
    
    @Provides
    fun provideExerciseDao(database: MacrosDatabase): ExerciseDao = database.exerciseDao()
    
    @Provides
    fun provideGeminiAnalysisDao(database: MacrosDatabase): GeminiAnalysisDao = database.geminiAnalysisDao()
}
