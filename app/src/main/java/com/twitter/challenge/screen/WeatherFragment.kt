package com.twitter.challenge.screen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.twitter.challenge.R
import com.twitter.challenge.model.TemperatureConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.weather_fragment) {

    private lateinit var temperatureTexView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var cloudImage: ImageView
    private lateinit var loadingTextView: TextView
    private lateinit var nextFiveButton: Button
    private lateinit var standardDevTextView: TextView
    private lateinit var currentWeatherGroup: Group
    private lateinit var standardDeviationGroup: Group

    private val viewModel: WeatherViewModel by viewModels()

    private val nDays: IntRange = 1..5

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        temperatureTexView = view.findViewById(R.id.temperature)
        windSpeedTextView = view.findViewById(R.id.wind_speed)
        cloudImage = view.findViewById(R.id.cloud_icon)
        loadingTextView = view.findViewById(R.id.loading_text)
        nextFiveButton = view.findViewById(R.id.next_five_button)
        standardDevTextView = view.findViewById(R.id.standard_deviation)
        currentWeatherGroup = view.findViewById(R.id.current_weather_group)
        standardDeviationGroup = view.findViewById(R.id.standard_deviation_group)

        nextFiveButton.setOnClickListener {
            viewModel.fetchNextNDays(nDays)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherState.collect { state -> state.updateScreen() }
            }
        }
    }

    private fun WeatherViewModel.WeatherState.updateScreen() {
        if (weatherResult != null) {
            loadingTextView.isVisible = false
            currentWeatherGroup.isVisible = true
            nextFiveButton.isVisible = true
            nextFiveButton.text = getString(R.string.next_five_days)
            val currentTemp = weatherResult.weather.currentWeather.temp
            temperatureTexView.text = getString(R.string.temperature, currentTemp, TemperatureConverter.celsiusToFahrenheit(currentTemp.toFloat()))
            val windSpeed = weatherResult.weather.wind.speed
            windSpeedTextView.text = getString(R.string.wind_speed, windSpeed)

            if (weatherResult.weather.clouds.cloudiness > 50) {
                cloudImage.isVisible = true
            }
            // stdDev returned
            if (weatherResult.stdDev > -1) {
                standardDeviationGroup.isVisible = true
                nextFiveButton.isVisible = false
                standardDevTextView.text = weatherResult.stdDev.toString()
            }
        }
        if (weatherloading) {
            loadingTextView.isVisible = true
        }
        if (weatherError) {
            loadingTextView.text = getString(R.string.current_weather_error)
        }
        if (nextFiveLoading) {
            nextFiveButton.isClickable = false
            nextFiveButton.text = getString(R.string.loading)
            standardDeviationGroup.isVisible = false
        }
        if (stdDevError) {
            nextFiveButton.text = getString(R.string.next_five_days)
            nextFiveButton.isClickable = true
            standardDeviationGroup.isVisible = true
            standardDevTextView.text = getString(R.string.std_dev_error)
        }
    }
}