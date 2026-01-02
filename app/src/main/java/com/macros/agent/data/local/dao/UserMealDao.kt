package com.macros.agent.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Embedded
import androidx.room.Relation
import com.macros.agent.data.local.entity.UserMeal
import com.macros.agent.data.local.entity.UserMealItem
import kotlinx.coroutines.flow.Flow

data class UserMealWithItems(
    @Embedded val meal: UserMeal,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val items: List<UserMealItem>
)

@Dao
interface UserMealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: UserMeal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<UserMealItem>)

    @Transaction
    @Query("SELECT * FROM user_meals")
    fun getAllMealsWithItems(): Flow<List<UserMealWithItems>>

    @Transaction
    @Query("SELECT * FROM user_meals WHERE id = :id")
    suspend fun getMealWithItems(id: Long): UserMealWithItems?

    @Delete
    suspend fun deleteMeal(meal: UserMeal)

    @Query("DELETE FROM user_meal_items WHERE mealId = :mealId")
    suspend fun deleteItemsForMeal(mealId: Long)
}
