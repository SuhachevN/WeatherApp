package ru.suhachev.weatherapp.util

import ru.suhachev.weatherapp.R

object WeatherConditionMapper {
    fun getConditionResource(condition: String): Int {
        return when (condition.lowercase()) {
            "partly cloudy" -> R.string.weather_partly_cloudy
            "patchy light drizzle" -> R.string.weather_patchy_light_drizzle
            "sunny" -> R.string.weather_sunny
            "clear" -> R.string.weather_clear
            "patchy rain nearby" -> R.string.weather_patchy_rain_nearby
            else -> R.string.weather_clear // Default fallback
        }
    }
} 