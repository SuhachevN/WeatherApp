package ru.suhachev.weatherapp.data.mapper

import android.util.Log
import ru.suhachev.weatherapp.data.dto.*
import ru.suhachev.weatherapp.domain.model.*
import kotlin.math.roundToInt

fun WeatherDto.toDomain(cityName: String): Pair<List<WeatherModel>, WeatherModel> {
    val hourlyData = hourly
    val dailyData = daily
    val currentWeatherData = currentWeather

    val days = dailyData?.time?.mapIndexedNotNull { index, date ->
        val maxTemp = dailyData.temperatureMax?.getOrNull(index)
        val minTemp = dailyData.temperatureMin?.getOrNull(index)
        val weatherCode = dailyData.weatherCode?.getOrNull(index)
        
        if (date == null || maxTemp == null || minTemp == null) return@mapIndexedNotNull null

        val dayHours = hourlyData?.time?.mapIndexedNotNull { hourIndex, hourTime ->
            if (hourTime.startsWith(date)) {
                val temp = hourlyData.temperature?.getOrNull(hourIndex)
                val hourWeatherCode = hourlyData.weatherCode?.getOrNull(hourIndex)
                if (temp != null && hourWeatherCode != null) {
                    HourModel(
                        time = hourTime.substring(11, 16),
                        temp = temp.roundToInt().toString(),
                        condition = getWeatherDescription(hourWeatherCode),
                        icon = getWeatherIcon(hourWeatherCode)
                    )
                } else null
            } else null
        } ?: emptyList()
        
        WeatherModel(
            city = cityName,
            time = date,
            currentTemp = "",
            condition = getWeatherDescription(weatherCode ?: 0),
            icon = getWeatherIcon(weatherCode ?: 0),
            maxTemp = maxTemp.roundToInt().toString(),
            minTemp = minTemp.roundToInt().toString(),
            hours = dayHours
        )
    }?.distinctBy { it.time } ?: emptyList()
    
    Log.d("WeatherMapper", "Mapped ${days.size} days: ${days.map { it.time }}")

    val current = WeatherModel(
        city = cityName,
        time = currentWeatherData?.time ?: "",
        currentTemp = currentWeatherData?.temperature?.roundToInt()?.toString() ?: "",
        condition = getWeatherDescription(currentWeatherData?.weatherCode ?: 0),
        icon = getWeatherIcon(currentWeatherData?.weatherCode ?: 0),
        maxTemp = days.firstOrNull()?.maxTemp ?: "",
        minTemp = days.firstOrNull()?.minTemp ?: "",
        hours = days.firstOrNull()?.hours ?: emptyList()
    )
    
    return Pair(days, current)
}

fun getWeatherDescription(code: Int): String = when (code) {
    0 -> "Ясно"
    1, 2, 3 -> "Переменная облачность"
    45, 48 -> "Туман"
    51, 53, 55 -> "Морось"
    56, 57 -> "Ледяная морось"
    61, 63, 65 -> "Дождь"
    66, 67 -> "Ледяной дождь"
    71, 73, 75 -> "Снег"
    77 -> "Снежная крупа"
    80, 81, 82 -> "Ливень"
    85, 86 -> "Снегопад"
    95 -> "Гроза"
    96, 99 -> "Гроза с градом"
    else -> "Неизвестно"
}

fun getWeatherIcon(code: Int): String = ""

 