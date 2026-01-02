package com.macros.agent.data.remote.api

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleFitService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .build()

    fun hasPermissions(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return if (account != null) {
            GoogleSignIn.hasPermissions(account, fitnessOptions)
        } else {
            false
        }
    }

    suspend fun getDailySteps(date: LocalDate): Int {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return 0
        
        return try {
            val response = Fitness.getHistoryClient(context, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .await()
                
            val totalSteps = if (response.dataPoints.isEmpty()) {
                0
            } else {
                response.dataPoints[0].getValue(Field.FIELD_STEPS).asInt()
            }
                
            Log.d("GoogleFit", "Steps for today: $totalSteps")
            totalSteps
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error reading steps", e)
            0
        }
    }
    
    suspend fun getDailyCalories(date: LocalDate): Float {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return 0f
        
        return try {
            val response = Fitness.getHistoryClient(context, account)
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .await()
                
            val totalCals = if (response.dataPoints.isEmpty()) {
                0f
            } else {
                response.dataPoints[0].getValue(Field.FIELD_CALORIES).asFloat()
            }
                
            Log.d("GoogleFit", "Calories for today: $totalCals")
            totalCals
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error reading calories", e)
            0f
        }
    }
    
    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .addExtension(fitnessOptions)
            .build()
    }
}
