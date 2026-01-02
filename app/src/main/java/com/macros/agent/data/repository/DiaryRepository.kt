package com.macros.agent.data.repository

import com.macros.agent.data.local.dao.DiaryDao
import com.macros.agent.data.local.dao.DailyTotals
import com.macros.agent.data.local.dao.DateCalorie
import com.macros.agent.data.local.entity.DiaryEntry
import com.macros.agent.data.local.entity.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for diary entries.
 */
@Singleton
class DiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD
    
    /**
     * Format a LocalDate to the string format used in the database.
     */
    fun formatDate(date: LocalDate): String = date.format(dateFormatter)
    
    /**
     * Get today's date formatted for database queries.
     */
    fun getTodayString(): String = formatDate(LocalDate.now())
    
    /**
     * Get all diary entries for a specific date.
     */
    fun getEntriesForDate(date: LocalDate): Flow<List<DiaryEntry>> {
        return diaryDao.getEntriesForDate(formatDate(date))
    }
    
    /**
     * Get entries for a specific meal on a date.
     */
    fun getEntriesForMeal(date: LocalDate, mealType: MealType): Flow<List<DiaryEntry>> {
        return diaryDao.getEntriesForMeal(formatDate(date), mealType)
    }
    
    /**
     * Get daily macro totals for a date.
     */
    fun getDailyTotals(date: LocalDate): Flow<DailyTotals> {
        return diaryDao.getDailyTotals(formatDate(date))
    }
    
    /**
     * Get meal-specific totals.
     */
    fun getMealTotals(date: LocalDate, mealType: MealType): Flow<DailyTotals> {
        return diaryDao.getMealTotals(formatDate(date), mealType)
    }
    
    /**
     * Add a new diary entry.
     */
    suspend fun addEntry(entry: DiaryEntry): Long {
        return diaryDao.insert(entry)
    }
    
    /**
     * Add multiple entries (e.g., from Gemini analysis).
     */
    suspend fun addEntries(entries: List<DiaryEntry>): List<Long> {
        return diaryDao.insertAll(entries)
    }
    
    /**
     * Update an existing entry.
     */
    suspend fun updateEntry(entry: DiaryEntry) {
        diaryDao.update(entry)
    }
    
    /**
     * Delete an entry.
     */
    suspend fun deleteEntry(entry: DiaryEntry) {
        diaryDao.delete(entry)
    }
    
    /**
     * Delete entry by ID.
     */
    suspend fun deleteEntryById(id: Long) {
        diaryDao.deleteById(id)
    }
    
    /**
     * Get an entry by ID.
     */
    suspend fun getEntryById(id: Long): DiaryEntry? {
        return diaryDao.getById(id)
    }
    
    /**
     * Get entries for a date range (for weekly/monthly reports).
     */
    fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DiaryEntry>> {
        return diaryDao.getEntriesInRange(formatDate(startDate), formatDate(endDate))
    }
    
    /**
     * Get recent dates that have entries.
     */
    suspend fun getRecentDates(limit: Int = 30): List<LocalDate> {
        return diaryDao.getRecentDates(limit).mapNotNull { 
            try {
                LocalDate.parse(it, dateFormatter)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Get calorie history for a range of dates.
     */
    fun getCalorieHistory(startDate: LocalDate, endDate: LocalDate): Flow<List<DateCalorie>> {
        return diaryDao.getCalorieHistory(formatDate(startDate), formatDate(endDate))
    }
}
