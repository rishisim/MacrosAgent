package com.macros.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macros.agent.data.local.entity.ExerciseEntry
import com.macros.agent.data.local.entity.ExerciseSource
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ExerciseEntry): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<ExerciseEntry>)
    
    @Update
    suspend fun update(entry: ExerciseEntry)
    
    @Delete
    suspend fun delete(entry: ExerciseEntry)
    
    @Query("DELETE FROM exercise_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT * FROM exercise_entries WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntry?
    
    @Query("SELECT * FROM exercise_entries WHERE date = :date ORDER BY timestamp")
    fun getEntriesForDate(date: String): Flow<List<ExerciseEntry>>
    
    @Query("SELECT COALESCE(SUM(caloriesBurned), 0) FROM exercise_entries WHERE date = :date")
    fun getTotalCaloriesBurned(date: String): Flow<Int>
    
    @Query("SELECT * FROM exercise_entries WHERE googleFitActivityId = :activityId LIMIT 1")
    suspend fun getByGoogleFitId(activityId: String): ExerciseEntry?
    
    @Query("SELECT * FROM exercise_entries WHERE source = :source AND date = :date")
    suspend fun getBySourceForDate(source: ExerciseSource, date: String): List<ExerciseEntry>
    
    @Query("DELETE FROM exercise_entries WHERE source = :source AND date = :date")
    suspend fun deleteBySourceForDate(source: ExerciseSource, date: String)
}
