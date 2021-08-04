package com.twitter.challenge.datasource

import com.twitter.challenge.model.Weather
import com.twitter.challenge.network.WeatherAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val weatherAPI: WeatherAPI
) : WeatherDataSource {

    override fun currentWeather(): Flow<Weather> {
        return flow {
            val weather = weatherAPI.currentWeather()
            emit(weather)
        }
    }

    override fun futureWeatherForDays(days: IntRange): Flow<List<Weather>> {
        return flow {  }
    }
}