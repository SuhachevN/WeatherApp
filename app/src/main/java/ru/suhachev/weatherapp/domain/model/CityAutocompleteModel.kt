package ru.suhachev.weatherapp.domain.model

data class CityAutocompleteModel(
    val id: Long,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val population: Int? = null,
    val elevation: Double? = null
) {
    val displayName: String
        get() = if (country.isNotEmpty()) "$name, $country" else name
} 