package ru.suhachev.weatherapp.presentation.util

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ru.suhachev.weatherapp.data.dao.GeocodingDao
import ru.suhachev.weatherapp.data.entity.GeocodingCacheEntity
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AndroidGeocoder @Inject constructor(
    private val context: Context,
    private val geocodingDao: GeocodingDao
) {
    private val geocoder by lazy { 
        Geocoder(context, Locale.getDefault()) 
    }

    companion object {
        private const val CACHE_DURATION = 7 * 24 * 60 * 60 * 1000L // 7 дней
        private const val TAG = "AndroidGeocoder"
    }

    suspend fun getCityFromCoordinates(
        latitude: Double, 
        longitude: Double
    ): String = withContext(Dispatchers.IO) {
        val cacheKey = "%.6f,%.6f".format(latitude, longitude)
        
        // Проверяем кеш
        val cachedResult = geocodingDao.getGeocodingResult(cacheKey)
        if (cachedResult != null && 
            System.currentTimeMillis() - cachedResult.lastUpdated < CACHE_DURATION) {
            Log.d(TAG, "Returning cached city for $cacheKey: ${cachedResult.cityName}")
            return@withContext cachedResult.cityName
        }

        // Получаем из Geocoder
        val city = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Для Android 13+ используем асинхронный API
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val city = addresses.firstOrNull()?.let { address ->
                            // Приоритет: город -> населенный пункт -> административный район
                            address.locality 
                                ?: address.subAdminArea 
                                ?: address.adminArea
                                ?: formatCoordinates(latitude, longitude)
                        } ?: formatCoordinates(latitude, longitude)
                        
                        continuation.resume(city)
                    }
                }
            } else {
                // Для старых версий используем синхронный API
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { address ->
                    address.locality 
                        ?: address.subAdminArea 
                        ?: address.adminArea
                        ?: formatCoordinates(latitude, longitude)
                } ?: formatCoordinates(latitude, longitude)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder error for coordinates: $latitude, $longitude", e)
            // В случае ошибки (нет интернета, сервис недоступен) возвращаем координаты
            formatCoordinates(latitude, longitude)
        }

        // Сохраняем в кеш
        if (!city.contains(",")) { // Сохраняем только если получили название города
            geocodingDao.insertGeocodingResult(
                GeocodingCacheEntity(
                    query = cacheKey,
                    latitude = latitude,
                    longitude = longitude,
                    cityName = city
                )
            )
            // Чистим старые записи
            geocodingDao.deleteOldResults(System.currentTimeMillis() - CACHE_DURATION)
        }

        return@withContext city
    }

    suspend fun getCoordinatesFromCity(cityName: String): Pair<Double, Double>? = 
        withContext(Dispatchers.IO) {
            // Проверяем кеш
            val cachedResult = geocodingDao.getGeocodingResult(cityName.lowercase())
            if (cachedResult != null && 
                System.currentTimeMillis() - cachedResult.lastUpdated < CACHE_DURATION) {
                Log.d(TAG, "Returning cached coordinates for $cityName")
                return@withContext Pair(cachedResult.latitude, cachedResult.longitude)
            }

            try {
                val coordinates = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocationName(cityName, 1) { addresses ->
                            val location = addresses.firstOrNull()?.let { address ->
                                Pair(address.latitude, address.longitude)
                            }
                            continuation.resume(location)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(cityName, 1)
                    addresses?.firstOrNull()?.let { address ->
                        Pair(address.latitude, address.longitude)
                    }
                }

                // Сохраняем в кеш
                coordinates?.let { (lat, lon) ->
                    geocodingDao.insertGeocodingResult(
                        GeocodingCacheEntity(
                            query = cityName.lowercase(),
                            latitude = lat,
                            longitude = lon,
                            cityName = cityName
                        )
                    )
                }

                coordinates
            } catch (e: Exception) {
                Log.e(TAG, "Geocoder error for city: $cityName", e)
                null
            }
        }

    private fun formatCoordinates(latitude: Double, longitude: Double): String {
        return "%.2f, %.2f".format(latitude, longitude)
    }
} 