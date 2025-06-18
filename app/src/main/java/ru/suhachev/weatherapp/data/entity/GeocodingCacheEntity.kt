package ru.suhachev.weatherapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geocoding_cache")
data class GeocodingCacheEntity(
    @PrimaryKey
    val query: String, // Может быть как "lat,lon", так и название города
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val lastUpdated: Long = System.currentTimeMillis()
) 