package ru.suhachev.weatherapp.domain.usecase

import ru.suhachev.weatherapp.domain.model.WeatherModel
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String, forceRefresh: Boolean = false): WeatherModel {
        return repository.getWeather(city, forceRefresh).second
    }
} 