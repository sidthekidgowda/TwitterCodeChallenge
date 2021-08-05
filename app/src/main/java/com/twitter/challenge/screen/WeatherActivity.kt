package com.twitter.challenge.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.twitter.challenge.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity(R.layout.weather_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WeatherFragment())
                .commit()
        }
    }
}