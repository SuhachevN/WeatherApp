package ru.suhachev.weatherapp.presentation.state

import ru.suhachev.weatherapp.domain.model.WeatherModel

data class WeatherState(
    val isLoading: Boolean = false,
    val days: List<WeatherModel> = emptyList(),
    val current: WeatherModel? = null,
    val error: String? = null,
    val selectedDayIndex: Int = 0
) 