package com.macros.agent.di

import com.macros.agent.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val USDA_BASE_URL = "https://api.nal.usda.gov/fdc/v1/"
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("usda")
    fun provideUsdaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(USDA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Named("usdaApiKey")
    fun provideUsdaApiKey(): String = BuildConfig.USDA_API_KEY
    
    @Provides
    @Named("geminiApiKey")
    fun provideGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY
    
    @Provides
    @Singleton
    fun provideUsdaApiService(@Named("usda") retrofit: Retrofit): com.macros.agent.data.remote.api.UsdaApiService {
        return retrofit.create(com.macros.agent.data.remote.api.UsdaApiService::class.java)
    }
}
