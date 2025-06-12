package ru.suhachev.weatherapp.data.mapper

import ru.suhachev.weatherapp.data.local.WeatherEntity
import ru.suhachev.weatherapp.domain.model.WeatherModel

fun WeatherEntity.toDomain(): Pair<List<WeatherModel>, WeatherModel> {
    val current = WeatherModel(
        city = city,
        time = time,
        currentTemp = currentTemp,
        condition = condition,
        icon = icon,
        maxTemp = maxTemp,
        minTemp = minTemp,
        hours = emptyList()
    )
    return Pair(listOf(current), current)
}

fun WeatherModel.toEntity(): WeatherEntity {
    return WeatherEntity(
        city = city,
        time = time,
        currentTemp = currentTemp,
        condition = condition,
        icon = icon,
        maxTemp = maxTemp,
        minTemp = minTemp
    )
} 