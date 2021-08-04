package com.twitter.challenge.datasource

import com.twitter.challenge.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherDataSource {

    fun currentWeather(): Flow<Weather>

    fun futureWeatherForDays(days: IntRange): Flow<List<Weather>>
}