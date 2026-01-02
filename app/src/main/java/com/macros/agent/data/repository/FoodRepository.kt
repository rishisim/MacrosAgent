package com.macros.agent.data.repository

import com.macros.agent.data.local.dao.FoodDao
import com.macros.agent.data.local.entity.Food
import com.macros.agent.data.remote.api.UsdaApiService
import com.macros.agent.data.remote.dto.UsdaNutrientIds
import com.macros.agent.data.remote.dto.UsdaSearchFood
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Repository for food data.
 * Handles local caching and USDA API integration.
 */
@Singleton
class FoodRepository @Inject constructor(
    private val foodDao: FoodDao,
    private val usdaApiService: UsdaApiService,
    @Named("usdaApiKey") private val apiKey: String
) {
    /**
     * Search for foods - first checks local cache, then USDA API.
     * Results from API are cached locally for future use.
     */
    suspend fun searchFoods(query: String, forceRemote: Boolean = false): Result<List<Food>> {
        return try {
            // First check local cache
            if (!forceRemote) {
                val localResults = foodDao.search(query, limit = 50)
                if (localResults.isNotEmpty()) {
                    return Result.success(localResults)
                }
            }
            
            // Search USDA API
            val response = usdaApiService.searchFoods(
                apiKey = apiKey,
                query = query,
                pageSize = 25
            )
            
            // Convert and cache results
            val foods = response.foods.map { it.toFood() }
            foodDao.insertAll(foods)
            
            Result.success(foods)
        } catch (e: Exception) {
            // On error, try to return cached results
            val cached = foodDao.search(query)
            if (cached.isNotEmpty()) {
                Result.success(cached)
            } else {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Search for food by barcode.
     */
    suspend fun searchByBarcode(barcode: String): Result<Food?> {
        return try {
            // Check local cache first
            val cached = foodDao.getByBarcode(barcode)
            if (cached != null) {
                return Result.success(cached)
            }
            
            // Search USDA by barcode
            val response = usdaApiService.searchByBarcode(
                apiKey = apiKey,
                barcode = barcode
            )
            
            val food = response.foods.firstOrNull()?.toFood()?.copy(barcode = barcode)
            if (food != null) {
                foodDao.insert(food)
            }
            
            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get food by ID - checks cache first.
     */
    suspend fun getFoodById(fdcId: Int): Result<Food?> {
        return try {
            // Check local cache
            val cached = foodDao.getById(fdcId)
            if (cached != null) {
                return Result.success(cached)
            }
            
            // Fetch from API
            val response = usdaApiService.getFoodDetail(fdcId, apiKey)
            val servingSize = response.servingSize ?: 100f
            val ratio = if (servingSize > 0) 100f / servingSize else 1f
            
            val food = Food(
                fdcId = response.fdcId,
                description = response.description,
                brandOwner = response.brandOwner,
                brandName = response.brandName,
                calories = (response.labelNutrients?.calories?.value ?: 0f) * ratio,
                protein = (response.labelNutrients?.protein?.value ?: 0f) * ratio,
                carbs = (response.labelNutrients?.carbohydrates?.value ?: 0f) * ratio,
                fat = (response.labelNutrients?.fat?.value ?: 0f) * ratio,
                fiber = (response.labelNutrients?.fiber?.value ?: 0f) * ratio,
                sugar = (response.labelNutrients?.sugars?.value ?: 0f) * ratio,
                sodium = (response.labelNutrients?.sodium?.value ?: 0f) * ratio,
                servingSize = servingSize,
                servingUnit = response.servingSizeUnit ?: "g",
                category = response.foodCategory,
                ingredients = response.ingredients,
                barcode = response.gtinUpc
            )
            
            foodDao.insert(food)
            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Favorites
    fun getFavorites(): Flow<List<Food>> = foodDao.getFavorites()
    
    suspend fun toggleFavorite(fdcId: Int, isFavorite: Boolean) {
        foodDao.setFavorite(fdcId, isFavorite)
    }
    
    // Recent foods
    fun getRecentFoods(limit: Int = 20): Flow<List<Food>> = foodDao.getRecent(limit)
    
    suspend fun updateFoodUsage(fdcId: Int) {
        foodDao.updateUsage(fdcId)
    }
    
    // Custom foods
    fun getCustomFoods(): Flow<List<Food>> = foodDao.getCustomFoods()
    
    suspend fun saveCustomFood(food: Food) {
        foodDao.insert(food.copy(isCustom = true))
    }
    
    suspend fun getFood(fdcId: Int): Food? = getFoodById(fdcId).getOrNull()

    fun getFoodFlow(fdcId: Int): Flow<Food?> = foodDao.getByIdFlow(fdcId)
}

/**
 * Extension to convert USDA search result to local Food entity.
 */
private fun UsdaSearchFood.toFood(): Food {
    val nutrients = foodNutrients ?: emptyList()
    
    fun findNutrient(id: Int): Float {
        return nutrients.find { it.nutrientId == id }?.value ?: 0f
    }
    
    return Food(
        fdcId = fdcId,
        description = description,
        brandOwner = brandOwner,
        brandName = brandName,
        calories = findNutrient(UsdaNutrientIds.ENERGY_KCAL),
        protein = findNutrient(UsdaNutrientIds.PROTEIN),
        carbs = findNutrient(UsdaNutrientIds.CARBOHYDRATES),
        fat = findNutrient(UsdaNutrientIds.TOTAL_FAT),
        fiber = findNutrient(UsdaNutrientIds.FIBER),
        sugar = findNutrient(UsdaNutrientIds.SUGARS),
        sodium = findNutrient(UsdaNutrientIds.SODIUM),
        servingSize = servingSize ?: 100f,
        servingUnit = servingSizeUnit ?: "g",
        category = foodCategory,
        ingredients = ingredients,
        barcode = gtinUpc
    )
}
