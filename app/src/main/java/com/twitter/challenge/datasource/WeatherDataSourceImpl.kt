package com.twitter.challenge.datasource

import android.util.Log
import com.twitter.challenge.model.Weather
import com.twitter.challenge.model.WeatherResponse
import com.twitter.challenge.network.WeatherAPI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

class WeatherDataSourceImpl @Inject constructor(
    private val weatherAPI: WeatherAPI,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : WeatherDataSource {

    companion object {
        private val TAG = WeatherDataSourceImpl::class.java.simpleName
    }

    private val scope = CoroutineScope(SupervisorJob())

    private val currentWeather: SharedFlow<WeatherResponse> = fetchCurrentWeather()
        .shareIn(
            scope = scope,
            replay = 1,
            started = SharingStarted.WhileSubscribed(5000)
        )

    override fun currentWeather(): Flow<WeatherResponse> {
        return currentWeather
    }

    private fun fetchCurrentWeather(): Flow<WeatherResponse> {
        return flow<WeatherResponse> {
            val weather = weatherAPI.currentWeather()
            emit(WeatherResponse.Success(weather))
        }.catch { t ->
            Log.e(TAG, t.message ?: "Failed fetching current weather")
            emit(WeatherResponse.Failure(t))
        }
        .flowOn(ioDispatcher)
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