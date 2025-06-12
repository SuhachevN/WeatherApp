    package ru.suhachev.weatherapp.data.mapper

import ru.suhachev.weatherapp.data.model.*
import ru.suhachev.weatherapp.domain.model.*

fun WeatherDto.toDomain(): Pair<List<WeatherModel>, WeatherModel> {
    val days = forecast.forecastday.map { dayDto ->
        WeatherModel(
            city = location.name,
            time = dayDto.date,
            currentTemp = "",
            condition = dayDto.day.condition.text,
            icon = "https:" + dayDto.day.condition.icon,
            maxTemp = dayDto.day.maxtemp_c.toInt().toString(),
            minTemp = dayDto.day.mintemp_c.toInt().toString(),
            hours = dayDto.hour.map { it.toDomain() }
        )
    }
    val current = WeatherModel(
        city = location.name,
        time = current.last_updated,
        currentTemp = current.temp_c.toInt().toString(),
        condition = current.condition.text,
        icon = "https:" + current.condition.icon,
        maxTemp = days.firstOrNull()?.maxTemp ?: "",
        minTemp = days.firstOrNull()?.minTemp ?: "",
        hours = days.firstOrNull()?.hours ?: emptyList()
    )
    return Pair(days, current)
}

fun HourDto.toDomain(): HourModel = HourModel(
    time = time.split(" ").getOrNull(1) ?: time,
    temp = temp_c.toInt().toString(),
    condition = condition.text,
    icon = "https:" + condition.icon
) 