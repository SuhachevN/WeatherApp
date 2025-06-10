package ru.suhachev.weatherapp.domain.usecase

import ru.suhachev.weatherapp.domain.model.WeatherModel
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetHourlyWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): List<WeatherModel> {
        return repository.getWeather(city).first
    }
} 