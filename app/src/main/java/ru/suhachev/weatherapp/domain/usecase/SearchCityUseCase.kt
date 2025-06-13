package ru.suhachev.weatherapp.domain.usecase

import ru.suhachev.weatherapp.data.model.CitySearchDto
import ru.suhachev.weatherapp.data.network.WeatherApiService
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val api: WeatherApiService,
    private val apiKey: String
) {
    suspend operator fun invoke(query: String): List<CitySearchDto> {
        return api.searchCity(apiKey = apiKey, query = query)
    }
} 