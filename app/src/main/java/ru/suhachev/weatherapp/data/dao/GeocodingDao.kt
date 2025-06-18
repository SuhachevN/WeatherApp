package ru.suhachev.weatherapp.data.dao

import androidx.room.*
import ru.suhachev.weatherapp.data.entity.GeocodingCacheEntity

@Dao
interface GeocodingDao {
    @Query("SELECT * FROM geocoding_cache WHERE `query` = :query")
    suspend fun getGeocodingResult(query: String): GeocodingCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeocodingResult(result: GeocodingCacheEntity)

    @Query("DELETE FROM geocoding_cache WHERE lastUpdated < :timestamp")
    suspend fun deleteOldResults(timestamp: Long)

    @Query("DELETE FROM geocoding_cache")
    suspend fun clearCache()
}