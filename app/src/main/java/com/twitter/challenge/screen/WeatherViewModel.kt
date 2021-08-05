package com.twitter.challenge.screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twitter.challenge.datasource.WeatherDataSource
import com.twitter.challenge.model.Weather
import com.twitter.challenge.model.WeatherResponse
import com.twitter.challenge.model.calculateStandardDeviation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Named("default")
    private val computationDispatcher: CoroutineDispatcher,
    private val weatherDataSource: WeatherDataSource
) : ViewModel() {

    companion object {
        private const val KEY_WEATHER_STATE = "weather_state"
        private val TAG = WeatherViewModel::class.java.simpleName
    }

    data class WeatherResult(
        val weather: Weather,
        val stdDev: Double
    ) : Serializable

    data class WeatherState(
            val weatherloading: Boolean,
            val weatherError: Boolean,
            val weatherResult: WeatherResult?,
            val nextFiveLoading: Boolean,
            val stdDevError: Boolean
    ) : Serializable

    private val _weatherState = MutableStateFlow(
        savedStateHandle.get(KEY_WEATHER_STATE) ?: WeatherState(
            weatherloading = true,
            weatherError = false,
            weatherResult = null,
            nextFiveLoading = false,
            stdDevError = false
    ))

    val weatherState: Flow<WeatherState> = _weatherState

    init {
        viewModelScope.launch {
            _weatherState.collect { savedStateHandle.set(KEY_WEATHER_STATE, it) }
        }

        viewModelScope.launch {
            weatherDataSource.currentWeather()
                    .map {
                        when(it) {
                            is WeatherResponse.Failure ->
                                WeatherState(
                                    weatherloading = false,
                                    weatherError = true,
                                    weatherResult = null,
                                    nextFiveLoading = false,
                                    stdDevError = false
                                )
                            is WeatherResponse.Success ->
                                _weatherState.value.copy(
                                    weatherloading = false,
                                    weatherResult = WeatherResult(
                                        it.weather,
                                        _weatherState.value.weatherResult?.stdDev ?: -1.0
                                    )
                                )
                        }
                    }
                    .flowOn(computationDispatcher)
                    .collect { weatherState ->
                        _weatherState.value = weatherState
                    }
        }
    }

    fun fetchNextNDays(days: IntRange) {
        _weatherState.value = _weatherState.value.copy(
            nextFiveLoading = true,
            stdDevError = false
        )
        viewModelScope.launch {
            weatherDataSource.futureWeatherForDays(days = days)
                    .map { it.calculateStandardDeviation() }
                    .map { stdDev ->
                        //std dev error could be true from previous fetch, set to false
                        //when refetching
                        _weatherState.value.copy(
                            nextFiveLoading = false,
                            weatherResult = _weatherState.value.weatherResult!!.copy(
                                    stdDev = stdDev
                            ),
                            stdDevError = false
                        )
                    }
                    .catch { e ->
                        Log.e(TAG, e.message ?: e.localizedMessage)
                        emit(_weatherState.value.copy(
                                nextFiveLoading = false,
                                stdDevError = true
                        ))
                    }
                    .flowOn(computationDispatcher)
                    .collect { weatherState ->
                        _weatherState.value = weatherState
                    }
        }
    }

    private fun List<Weather>.calculateStandardDeviation(): Double {
        return map { it.currentWeather.temp }.calculateStandardDeviation()
    }

    override fun onCleared() {
        // reset next five loading in case of process death to allow stdDev to be fetched again
        _weatherState.value = _weatherState.value.copy(nextFiveLoading = false)
        super.onCleared()
    }
}