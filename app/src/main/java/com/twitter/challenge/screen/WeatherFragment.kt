package com.twitter.challenge.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.twitter.challenge.R
import com.twitter.challenge.WeatherViewModel

class WeatherFragment : Fragment(R.layout.weather_fragment) {

    private val viewModel: WeatherViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
                    /**
             *     final TextView temperatureView = (TextView) findViewById(R.id.temperature);
            temperatureView.setText(getString(R.string.temperature, 34f, TemperatureConverter.celsiusToFahrenheit(34)));
             */
         */
    }
}