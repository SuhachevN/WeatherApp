package ru.suhachev.weatherapp.presentation.util

import ru.suhachev.weatherapp.R

object WeatherConditionMapper {
    fun getConditionResource(condition: String): Int {
        return when {
            condition.contains("Ясно", ignoreCase = true) -> R.string.weather_clear
            condition.contains("Переменная облачность", ignoreCase = true) -> R.string.weather_partly_cloudy
            condition.contains("Туман", ignoreCase = true) -> R.string.weather_mist
            condition.contains("Морось", ignoreCase = true) -> R.string.weather_patchy_light_drizzle
            condition.contains("Ледяная морось", ignoreCase = true) -> R.string.weather_patchy_light_drizzle
            condition.contains("Дождь", ignoreCase = true) && !condition.contains("Ливень") -> R.string.weather_patchy_light_rain
            condition.contains("Ледяной дождь", ignoreCase = true) -> R.string.weather_patchy_light_rain
            condition.contains("Снег", ignoreCase = true) && !condition.contains("Снегопад") -> R.string.weather_snow
            condition.contains("Снежная крупа", ignoreCase = true) -> R.string.weather_snow
            condition.contains("Ливень", ignoreCase = true) -> R.string.weather_heavy_rain
            condition.contains("Снегопад", ignoreCase = true) -> R.string.weather_heavy_snow
            condition.contains("Гроза", ignoreCase = true) && condition.contains("град", ignoreCase = true) -> R.string.weather_thunderstorm
            condition.contains("Гроза", ignoreCase = true) -> R.string.weather_thunder
            condition.contains("Неизвестно", ignoreCase = true) -> R.string.weather_clear
            else -> R.string.weather_clear
        }
    }
} 