package ru.suhachev.weatherapp.domain.model

data class WeatherModel(
    val city: String,
    val time: String,
    val currentTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: List<HourModel>
)

data class HourModel(
    val time: String,
    val temp: String,
    val condition: String,
    val icon: String
) 