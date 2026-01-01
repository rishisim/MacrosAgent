package com.macros.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macros.agent.data.local.entity.UserGoals
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goals: UserGoals)
    
    @Update
    suspend fun update(goals: UserGoals)
    
    @Query("SELECT * FROM user_goals WHERE id = 1")
    fun getGoals(): Flow<UserGoals?>
    
    @Query("SELECT * FROM user_goals WHERE id = 1")
    suspend fun getGoalsOnce(): UserGoals?
}
