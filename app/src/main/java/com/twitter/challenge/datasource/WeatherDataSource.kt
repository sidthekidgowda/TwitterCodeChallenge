package com.twitter.challenge.datasource

import com.twitter.challenge.model.Weather
import com.twitter.challenge.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherDataSource {

    fun currentWeather(): Flow<WeatherResponse>

    fun futureWeatherForDays(days: IntRange): Flow<List<Weather>>
}