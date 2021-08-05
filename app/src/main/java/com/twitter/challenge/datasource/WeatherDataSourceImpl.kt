package com.twitter.challenge.datasource

import com.twitter.challenge.model.Weather
import com.twitter.challenge.network.WeatherAPI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named

class WeatherDataSourceImpl @Inject constructor(
    private val weatherAPI: WeatherAPI,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : WeatherDataSource {

    override fun currentWeather(): Flow<Weather> {
        return flow {
            val weather = weatherAPI.currentWeather()
            emit(weather)
        }.flowOn(ioDispatcher)
    }

    override fun futureWeatherForDays(days: IntRange): Flow<List<Weather>> {
        return flow {
            coroutineScope {
                val futureWeatherDeferred = days.map {
                    async { weatherAPI.futureWeatherForDay(it) }
                }.toList()
                val futureWeather = futureWeatherDeferred.awaitAll()
                emit(futureWeather)
            }
        }.flowOn(ioDispatcher)
    }
}