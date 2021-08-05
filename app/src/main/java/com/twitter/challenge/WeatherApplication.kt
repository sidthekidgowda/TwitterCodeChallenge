package com.twitter.challenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class WeatherApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())
}