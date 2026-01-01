package com.macros.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.MealType
import kotlinx.coroutines.flow.Flow

/**
 * Data class for daily macro totals.
 */
data class DailyTotals(
    val totalCalories: Float,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFat: Float
)

@Dao
interface DiaryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryEntry): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DiaryEntry>): List<Long>
    
    @Update
    suspend fun update(entry: DiaryEntry)
    
    @Delete
    suspend fun delete(entry: DiaryEntry)
    
    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getById(id: Long): DiaryEntry?
    
    @Query("SELECT * FROM diary_entries WHERE date = :date ORDER BY mealType, timestamp")
    fun getEntriesForDate(date: String): Flow<List<DiaryEntry>>
    
    @Query("SELECT * FROM diary_entries WHERE date = :date AND mealType = :mealType ORDER BY timestamp")
    fun getEntriesForMeal(date: String, mealType: MealType): Flow<List<DiaryEntry>>
    
    @Query("""
        SELECT 
            COALESCE(SUM(calories), 0) as totalCalories,
            COALESCE(SUM(protein), 0) as totalProtein,
            COALESCE(SUM(carbs), 0) as totalCarbs,
            COALESCE(SUM(fat), 0) as totalFat
        FROM diary_entries 
        WHERE date = :date
    """)
    fun getDailyTotals(date: String): Flow<DailyTotals>
    
    @Query("""
        SELECT 
            COALESCE(SUM(calories), 0) as totalCalories,
            COALESCE(SUM(protein), 0) as totalProtein,
            COALESCE(SUM(carbs), 0) as totalCarbs,
            COALESCE(SUM(fat), 0) as totalFat
        FROM diary_entries 
        WHERE date = :date AND mealType = :mealType
    """)
    fun getMealTotals(date: String, mealType: MealType): Flow<DailyTotals>
    
    @Query("SELECT DISTINCT date FROM diary_entries ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentDates(limit: Int = 30): List<String>
    
    @Query("SELECT * FROM diary_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date, mealType, timestamp")
    fun getEntriesInRange(startDate: String, endDate: String): Flow<List<DiaryEntry>>
    
    @Query("DELETE FROM diary_entries WHERE date = :date")
    suspend fun deleteAllForDate(date: String)
}
