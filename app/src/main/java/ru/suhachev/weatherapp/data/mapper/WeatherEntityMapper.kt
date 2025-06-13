package ru.suhachev.weatherapp.data.mapper

import ru.suhachev.weatherapp.data.local.ForecastDayEntity
import ru.suhachev.weatherapp.data.local.HourEntity
import ru.suhachev.weatherapp.data.local.WeatherEntity
import ru.suhachev.weatherapp.domain.model.HourModel
import ru.suhachev.weatherapp.domain.model.WeatherModel

fun WeatherEntity.toDomain(hours: List<HourModel>): WeatherModel {
    return WeatherModel(
        city = city,
        time = time,
        currentTemp = currentTemp,
        condition = condition,
        icon = icon,
        maxTemp = maxTemp,
        minTemp = minTemp,
        hours = hours
    )
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

fun ForecastDayEntity.toDomain(hours: List<HourModel>): WeatherModel {
    return WeatherModel(
        city = city,
        time = date,
        currentTemp = currentTemp,
        condition = condition,
        icon = icon,
        maxTemp = maxTemp,
        minTemp = minTemp,
        hours = hours
    )
}

fun WeatherModel.toForecastDayEntity(): ForecastDayEntity {
    return ForecastDayEntity(
        city = city,
        date = time,
        currentTemp = currentTemp,
        condition = condition,
        icon = icon,
        maxTemp = maxTemp,
        minTemp = minTemp
    )
}

fun HourEntity.toDomain(): HourModel {
    return HourModel(
        time = time,
        temp = temp,
        condition = condition,
        icon = icon
    )
}

fun HourModel.toEntity(forecastDayId: Long, date: String): HourEntity {
    return HourEntity(
        forecastDayId = forecastDayId,
        date = date,
        time = time,
        temp = temp,
        condition = condition,
        icon = icon
    )
} 