package ru.suhachev.weatherapp.domain.repository

import ru.suhachev.weatherapp.domain.model.WeatherModel

interface WeatherRepository {
    suspend fun getWeather(city: String): Pair<List<WeatherModel>, WeatherModel>
} 