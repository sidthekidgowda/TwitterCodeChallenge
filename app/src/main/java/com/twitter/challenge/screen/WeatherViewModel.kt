package com.twitter.challenge.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twitter.challenge.datasource.WeatherDataSource
import com.twitter.challenge.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Named("io")
    private val ioDispatcher: CoroutineDispatcher,
    @Named("default")
    private val computationDispatcher: CoroutineDispatcher,
    private val weatherDataSource: WeatherDataSource
) : ViewModel() {

    companion object {
        private const val KEY_WEATHER_STATE = "weather_state"
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
            weatherState.collect { savedStateHandle.set(KEY_WEATHER_STATE, it) }
        }

        viewModelScope.launch {
            weatherDataSource.currentWeather()
                    .map { _weatherState.value.copy(
                            weatherloading = false,
                            weatherResult = WeatherResult(it, -1.0)
                    ) }
                    .catch {
                        emit(
                                WeatherState(
                                        weatherloading = false,
                                        weatherError = true,
                                        weatherResult = null,
                                        nextFiveLoading = false,
                                        stdDevError = false
                                ))
                    }
                    .flowOn(computationDispatcher)
                    .collect { weatherState ->
                        _weatherState.value = weatherState
            }
        }
    }
}