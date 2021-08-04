package com.twitter.challenge.model

import com.squareup.moshi.Json

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
)

data class Coordinate(
    val lon: Double,
    val lat: Double
)

data class CurrentWeather(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Rain(
    @Json(name = "3h")
    val lastThreeHours: Int
)

data class Clouds(
    val cloudiness: Int
)