package com.example.mediapipe.examples.macrosagent.di

import com.example.mediapipe.examples.macrosagent.data.GeminiRepository
import com.example.mediapipe.examples.macrosagent.service.VisionAgent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVisionAgent(): VisionAgent {
        return VisionAgent()
    }

    @Provides
    @Singleton
    fun provideGeminiRepository(): GeminiRepository {
        return GeminiRepository() // Note: If GeminiRepository needs VisionAgent, we should inject it. 
        // For now, based on existing code, GeminiRepository instantiates its own dependencies or is simple.
        // Let's check GeminiRepository source to be sure.
    }
}
