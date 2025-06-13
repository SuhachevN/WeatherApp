package ru.suhachev.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WeatherEntity::class, ForecastDayEntity::class, HourEntity::class],
    version = 2
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
} 