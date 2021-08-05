package com.twitter.challenge.model

import com.squareup.moshi.Json
import java.io.Serializable

data class Weather(
    @Json(name = "coord")
    val coordinate: Coordinate,
    @Json(name = "weather")
    val currentWeather: CurrentWeather,
    val wind: Wind,
    val rain: Rain,
    val clouds: Clouds,
    @Json(name = "name")
    val city: String
) : Serializable

data class Coordinate(
    val lon: Double,
    val lat: Double
) : Serializable

data class CurrentWeather(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
) : Serializable

data class Wind(
    val speed: Double,
    val deg: Int
) : Serializable

data class Rain(
    @Json(name = "3h")
    val lastThreeHours: Int
) : Serializable

data class Clouds(
    val cloudiness: Int
) : Serializable

sealed class WeatherResponse {
    data class Success(val weather: Weather): WeatherResponse()
    data class Failure(val t: Throwable?) : WeatherResponse()
}
