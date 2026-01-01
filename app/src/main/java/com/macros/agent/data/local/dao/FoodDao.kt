package com.macros.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.macros.agent.data.local.entity.Food
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<Food>)
    
    @Update
    suspend fun update(food: Food)
    
    @Delete
    suspend fun delete(food: Food)
    
    @Query("SELECT * FROM foods WHERE fdcId = :fdcId")
    suspend fun getById(fdcId: Int): Food?
    
    @Query("SELECT * FROM foods WHERE fdcId = :fdcId")
    fun getByIdFlow(fdcId: Int): Flow<Food?>
    
    @Query("SELECT * FROM foods WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): Food?
    
    @Query("""
        SELECT * FROM foods 
        WHERE description LIKE '%' || :query || '%' 
           OR brandOwner LIKE '%' || :query || '%'
           OR brandName LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN isFavorite THEN 0 ELSE 1 END,
            useCount DESC,
            lastUsed DESC
        LIMIT :limit
    """)
    suspend fun search(query: String, limit: Int = 50): List<Food>
    
    @Query("SELECT * FROM foods WHERE isFavorite = 1 ORDER BY useCount DESC")
    fun getFavorites(): Flow<List<Food>>
    
    @Query("SELECT * FROM foods WHERE useCount > 0 ORDER BY lastUsed DESC LIMIT :limit")
    fun getRecent(limit: Int = 20): Flow<List<Food>>
    
    @Query("SELECT * FROM foods WHERE isCustom = 1 ORDER BY createdAt DESC")
    fun getCustomFoods(): Flow<List<Food>>
    
    @Query("""
        UPDATE foods 
        SET lastUsed = :timestamp, useCount = useCount + 1 
        WHERE fdcId = :fdcId
    """)
    suspend fun updateUsage(fdcId: Int, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE foods SET isFavorite = :isFavorite WHERE fdcId = :fdcId")
    suspend fun setFavorite(fdcId: Int, isFavorite: Boolean)
    
    @Query("SELECT COUNT(*) FROM foods")
    suspend fun getCount(): Int
}
