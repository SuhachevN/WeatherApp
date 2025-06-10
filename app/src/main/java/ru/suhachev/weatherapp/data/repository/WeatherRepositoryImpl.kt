package ru.suhachev.weatherapp.data.repository

import ru.suhachev.weatherapp.data.mapper.toDomain
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.domain.model.WeatherModel
import ru.suhachev.weatherapp.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: WeatherApiService,
    private val apiKey: String
) : WeatherRepository {
    override suspend fun getWeather(city: String): Pair<List<WeatherModel>, WeatherModel> {
        val dto = api.getWeather(apiKey = apiKey, city = city)
        return dto.toDomain()
    }
} 