package ru.suhachev.weatherapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_days")
data class ForecastDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val city: String,
    val date: String,
    val currentTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String
) 