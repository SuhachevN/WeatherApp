package ru.suhachev.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("current_weather") val currentWeather: CurrentWeatherDto?,
    @SerializedName("hourly") val hourly: HourlyDto?,
    @SerializedName("daily") val daily: DailyDto?
)

data class CurrentWeatherDto(
    @SerializedName("temperature") val temperature: Double?,
    @SerializedName("weathercode") val weatherCode: Int?,
    @SerializedName("time") val time: String?
)

data class HourlyDto(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("temperature_2m") val temperature: List<Double>?,
    @SerializedName("weathercode") val weatherCode: List<Int>?
)

data class DailyDto(
    @SerializedName("time") val time: List<String>?,
    @SerializedName("weathercode") val weatherCode: List<Int>?,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>?,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>?
) 