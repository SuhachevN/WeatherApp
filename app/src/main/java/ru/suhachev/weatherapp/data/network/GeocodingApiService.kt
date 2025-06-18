package ru.suhachev.weatherapp.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.suhachev.weatherapp.data.dto.GeocodingDto

interface GeocodingApiService {
    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "ru",
        @Query("format") format: String = "json"
    ): GeocodingDto
} 