package ru.suhachev.weatherapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.suhachev.weatherapp.data.entity.ForecastDayEntity
import ru.suhachev.weatherapp.data.entity.HourEntity
import ru.suhachev.weatherapp.data.entity.WeatherEntity
import ru.suhachev.weatherapp.data.entity.GeocodingCacheEntity
import ru.suhachev.weatherapp.data.dao.WeatherDao
import ru.suhachev.weatherapp.data.dao.GeocodingDao

@Database(
    entities = [
        WeatherEntity::class, 
        ForecastDayEntity::class, 
        HourEntity::class,
        GeocodingCacheEntity::class
    ],
    version = 3
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun geocodingDao(): GeocodingDao
} 