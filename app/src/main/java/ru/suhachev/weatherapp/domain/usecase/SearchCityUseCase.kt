package ru.suhachev.weatherapp.domain.usecase

import ru.suhachev.weatherapp.data.dto.GeocodingResultDto
import ru.suhachev.weatherapp.data.network.GeocodingApiService
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val geocodingApi: GeocodingApiService
) {
    suspend operator fun invoke(query: String): List<GeocodingResultDto> {
        val result = geocodingApi.searchCity(name = query)
        return result.results ?: emptyList()
    }
} 