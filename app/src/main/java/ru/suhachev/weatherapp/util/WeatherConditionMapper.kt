package ru.suhachev.weatherapp.util

import ru.suhachev.weatherapp.R

object WeatherConditionMapper {
    fun getConditionResource(condition: String): Int {
        return when (condition.lowercase()) {
            "cloudy" -> R.string.weather_cloudy
            "partly cloudy" -> R.string.weather_partly_cloudy
            "patchy light drizzle" -> R.string.weather_patchy_light_drizzle
            "sunny" -> R.string.weather_sunny
            "clear" -> R.string.weather_clear
            "patchy rain nearby" -> R.string.weather_patchy_rain_nearby
            "thunder" -> R.string.weather_thunder
            "thunderstorm" -> R.string.weather_thunderstorm
            "lightning" -> R.string.weather_lightning
            "mist" -> R.string.weather_mist
            "thundery outbreaks in nearby" -> R.string.weather_thundery_outbreaks
            "patchy light rain in area with thunder" -> R.string.weather_patchy_light_rain_thunder
            "patchy light rain" -> R.string.weather_patchy_light_rain
            else -> R.string.weather_clear
        }
    }
} 