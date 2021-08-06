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

    // reset next five loading to false if process gets killed
    private val _weatherState = MutableStateFlow(
        savedStateHandle.get<WeatherState>(KEY_WEATHER_STATE)?.copy(nextFiveLoading = false)
            ?: WeatherState(
                weatherloading = true,
                weatherError = false,
                weatherResult = null,
                nextFiveLoading = false,
                stdDevError = false
            )
    )

    val weatherState: Flow<WeatherState> = _weatherState

    init {
        viewModelScope.launch {
            _weatherState.collect { savedStateHandle.set(KEY_WEATHER_STATE, it) }
        }

        viewModelScope.launch {
            weatherDataSource.currentWeather()
                    .map { it.calculateState() }
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

    private fun WeatherResponse.calculateState(): WeatherState {
        val currentState = _weatherState.value
        return when(this) {
            is WeatherResponse.Failure -> {
                if (currentState.weatherResult != null) {
                    WeatherState(
                        weatherloading = false,
                        weatherError = false,
                        weatherResult = currentState.weatherResult.copy(),
                        nextFiveLoading = false,
                        stdDevError = false
                    )
                } else {
                    WeatherState(
                        weatherloading = false,
                        weatherError = true,
                        weatherResult = null,
                        nextFiveLoading = false,
                        stdDevError = false
                    )
                }
            }
            is WeatherResponse.Success ->
                currentState.copy(
                    weatherloading = false,
                    weatherError = false,
                    weatherResult = WeatherResult(
                        this.weather,
                        _weatherState.value.weatherResult?.stdDev ?: -1.0
                    ),
                    nextFiveLoading = false,
                    stdDevError = false
                )
        }
    }

    private fun List<Weather>.calculateStandardDeviation(): Double {
        return map { it.currentWeather.temp }.calculateStandardDeviation()
    }
}