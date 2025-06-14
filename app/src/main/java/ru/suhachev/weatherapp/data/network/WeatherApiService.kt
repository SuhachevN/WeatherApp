package ru.suhachev.weatherapp.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.suhachev.weatherapp.data.model.WeatherDto
import ru.suhachev.weatherapp.data.model.CitySearchDto

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherDto

    @GET("search.json")
    suspend fun searchCity(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<CitySearchDto>
} 