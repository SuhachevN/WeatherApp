package ru.suhachev.weatherapp.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.suhachev.weatherapp.data.dto.WeatherDto

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,weathercode",
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min",
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherDto
} 