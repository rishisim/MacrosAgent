package com.macros.agent

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * MacrosAgent Application class.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class MacrosApp : Application()
