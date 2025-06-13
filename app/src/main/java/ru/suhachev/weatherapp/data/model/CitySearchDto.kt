package ru.suhachev.weatherapp.data.model

data class CitySearchDto(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
) 