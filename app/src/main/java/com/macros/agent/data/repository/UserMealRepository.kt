package com.macros.agent.data.repository

import com.macros.agent.data.local.dao.UserMealDao
import com.macros.agent.data.local.dao.UserMealWithItems
import com.macros.agent.data.local.entity.UserMeal
import com.macros.agent.data.local.entity.UserMealItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserMealRepository @Inject constructor(
    private val userMealDao: UserMealDao
) {
    fun getAllMeals(): Flow<List<UserMealWithItems>> = userMealDao.getAllMealsWithItems()

    suspend fun saveMeal(name: String, items: List<UserMealItem>) {
        val totalCalories = items.sumOf { it.calories.toDouble() }.toFloat()
        val totalProtein = items.sumOf { it.protein.toDouble() }.toFloat()
        val totalCarbs = items.sumOf { it.carbs.toDouble() }.toFloat()
        val totalFat = items.sumOf { it.fat.toDouble() }.toFloat()

        val meal = UserMeal(
            name = name,
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalCarbs = totalCarbs,
            totalFat = totalFat
        )

        val mealId = userMealDao.insertMeal(meal)
        userMealDao.insertItems(items.map { it.copy(mealId = mealId) })
    }

    suspend fun deleteMeal(meal: UserMeal) {
        userMealDao.deleteItemsForMeal(meal.id)
        userMealDao.deleteMeal(meal)
    }

    suspend fun getMeal(id: Long) = userMealDao.getMealWithItems(id)
}
