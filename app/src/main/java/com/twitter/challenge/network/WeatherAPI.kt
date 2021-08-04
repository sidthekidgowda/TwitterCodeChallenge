package com.twitter.challenge.network

import com.twitter.challenge.model.Weather
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherAPI {

    companion object {
        const val BASE_URL = "https://twitter-code-challenge.s3.amazonaws.com/"
    }

    @GET("current.json")
    suspend fun currentWeather(): Weather

    @GET("future_{day}.json")
    suspend fun futureWeatherForDay(@Path("day") day: Int): Weather
}