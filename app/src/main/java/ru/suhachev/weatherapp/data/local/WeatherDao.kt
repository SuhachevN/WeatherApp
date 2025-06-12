package ru.suhachev.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE city = :city")
    suspend fun getWeather(city: String): WeatherEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
    
    @Query("DELETE FROM weather WHERE lastUpdated < :timestamp")
    suspend fun deleteOldWeather(timestamp: Long)
} 