package com.twitter.challenge

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.twitter.challenge.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class WeatherState {
    object Loading : WeatherState()
    data class Result(val current: Weather,
                      val stdDev: Double) : WeatherState()
    object Error : WeatherState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle
) : ViewModel() {


}