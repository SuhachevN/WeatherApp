package ru.suhachev.weatherapp.data.model

data class WeatherDto(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto
)

data class LocationDto(
    val name: String
)

data class CurrentDto(
    val last_updated: String,
    val temp_c: Double,
    val condition: ConditionDto
)

data class ForecastDto(
    val forecastday: List<ForecastDayDto>
)

data class ForecastDayDto(
    val date: String,
    val day: DayDto,
    val hour: List<HourDto>
)

data class DayDto(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: ConditionDto
)

data class HourDto(
    val time: String,
    val temp_c: Double,
    val condition: ConditionDto
)

data class ConditionDto(
    val text: String,
    val icon: String
) 