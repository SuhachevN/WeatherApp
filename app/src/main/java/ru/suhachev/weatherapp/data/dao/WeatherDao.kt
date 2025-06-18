package ru.suhachev.weatherapp.data.dao

import androidx.room.*
import ru.suhachev.weatherapp.data.entity.ForecastDayEntity
import ru.suhachev.weatherapp.data.entity.HourEntity
import ru.suhachev.weatherapp.data.entity.WeatherEntity

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE city = :city")
    fun getWeather(city: String): WeatherEntity?

    @Query("SELECT * FROM forecast_days WHERE city = :city ORDER BY date ASC")
    fun getForecastDays(city: String): List<ForecastDayEntity>

    @Query("SELECT * FROM hours WHERE forecastDayId = :forecastDayId ORDER BY time ASC")
    fun getHours(forecastDayId: Long): List<HourEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastDay(forecastDay: ForecastDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHours(hours: List<HourEntity>)

    @Query("DELETE FROM forecast_days WHERE city = :city")
    suspend fun deleteForecastDays(city: String)

    @Query("DELETE FROM weather WHERE lastUpdated < :timestamp")
    suspend fun deleteOldWeather(timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastDays(days: List<ForecastDayEntity>): List<Long>
} 