package com.macros.agent.data.remote.api

import com.macros.agent.data.remote.dto.UsdaFoodDetailResponse
import com.macros.agent.data.remote.dto.UsdaSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * USDA FoodData Central API service.
 * Base URL: https://api.nal.usda.gov/fdc/v1/
 * Rate limit: 1000 requests per hour
 */
interface UsdaApiService {
    
    /**
     * Search for foods by query string.
     * Searches across branded, foundation, and SR legacy foods.
     */
    @GET("foods/search")
    suspend fun searchFoods(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 25,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("dataType") dataType: String? = "Branded,Foundation,SR Legacy"
    ): UsdaSearchResponse
    
    /**
     * Get detailed food information by FDC ID.
     */
    @GET("food/{fdcId}")
    suspend fun getFoodDetail(
        @Path("fdcId") fdcId: Int,
        @Query("api_key") apiKey: String
    ): UsdaFoodDetailResponse
    
    /**
     * Search for food by barcode (UPC/GTIN).
     */
    @GET("foods/search")
    suspend fun searchByBarcode(
        @Query("api_key") apiKey: String,
        @Query("query") barcode: String,
        @Query("dataType") dataType: String = "Branded"
    ): UsdaSearchResponse
}
