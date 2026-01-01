package com.macros.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macros.agent.data.local.entity.GeminiAnalysis
import kotlinx.coroutines.flow.Flow

@Dao
interface GeminiAnalysisDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analysis: GeminiAnalysis): Long
    
    @Update
    suspend fun update(analysis: GeminiAnalysis)
    
    @Query("SELECT * FROM gemini_analyses WHERE id = :id")
    suspend fun getById(id: Long): GeminiAnalysis?
    
    @Query("SELECT * FROM gemini_analyses ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 20): Flow<List<GeminiAnalysis>>
    
    @Query("SELECT * FROM gemini_analyses WHERE photoUri = :photoUri ORDER BY timestamp DESC LIMIT 1")
    suspend fun getByPhotoUri(photoUri: String): GeminiAnalysis?
    
    @Query("DELETE FROM gemini_analyses WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
