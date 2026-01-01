package com.example.mediapipe.examples.macrosagent.domain

import org.json.JSONObject
import javax.inject.Inject

class FoodParser @Inject constructor() {

    fun parseFoodName(json: String): String {
        return try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")
            if (items != null && items.length() > 0) {
                items.getJSONObject(0).optString("name", "Food")
            } else {
                "Food"
            }
        } catch (e: Exception) {
            "Food"
        }
    }

    fun parseCalories(json: String): Int {
        return try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")
            if (items != null && items.length() > 0) {
                items.getJSONObject(0).optInt("calories", 0)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    fun parseProtein(json: String): Int {
        return try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")
            if (items != null && items.length() > 0) {
                items.getJSONObject(0).optInt("protein_g", 0)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    fun parseCarbs(json: String): Int {
        return try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")
            if (items != null && items.length() > 0) {
                items.getJSONObject(0).optInt("carbs_g", 0)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    fun parseFat(json: String): Int {
        return try {
            val jsonObject = JSONObject(json)
            val items = jsonObject.optJSONArray("items")
            if (items != null && items.length() > 0) {
                items.getJSONObject(0).optInt("fat_g", 0)
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}
