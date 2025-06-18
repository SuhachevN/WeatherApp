package ru.suhachev.weatherapp.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.suhachev.weatherapp.data.dao.WeatherDao
import ru.suhachev.weatherapp.data.mapper.toDomain
import ru.suhachev.weatherapp.data.mapper.toEntity
import ru.suhachev.weatherapp.data.mapper.toForecastDayEntity
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.data.network.GeocodingApiService
import ru.suhachev.weatherapp.domain.model.WeatherModel
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import ru.suhachev.weatherapp.presentation.util.AndroidGeocoder
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val geocodingApi: GeocodingApiService,
    private val dao: WeatherDao,
    private val androidGeocoder: AndroidGeocoder
) : WeatherRepository {
    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000L
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY = 500L
    }

    override suspend fun getWeather(city: String, forceRefresh: Boolean): Pair<List<WeatherModel>, WeatherModel> = withContext(Dispatchers.IO) {
        Log.d("WeatherRepository", "getWeather called: city=$city, forceRefresh=$forceRefresh")
        
        val currentTime = System.currentTimeMillis()
        val cachedWeather = dao.getWeather(city)
        val forecastDays = dao.getForecastDays(city)
        
        Log.d("WeatherRepository", "Cache check: hasCachedWeather=${cachedWeather != null}, forecastDaysCount=${forecastDays.size}")

        if (!forceRefresh && cachedWeather != null && forecastDays.isNotEmpty() &&
            currentTime - cachedWeather.lastUpdated < CACHE_DURATION) {
            Log.d("WeatherRepository", "Returning cached data")
            val days = forecastDays.map { day ->
                val hours = dao.getHours(day.id).map { it.toDomain() }
                day.toDomain(hours)
            }
            val current = days.firstOrNull() ?: cachedWeather.toDomain(emptyList())
            try {
                updateWeatherFromApi(city)
            } catch (_: Exception) {}
            return@withContext Pair(days, current)
        }

        Log.d("WeatherRepository", "Cache miss or force refresh, loading from API")
        return@withContext updateWeatherFromApi(city)
    }

    private suspend fun updateWeatherFromApi(city: String): Pair<List<WeatherModel>, WeatherModel> = withContext(Dispatchers.IO) {
        Log.d("WeatherRepository", "updateWeatherFromApi called for city: $city")
        var retryCount = 0
        var lastException: Exception? = null
        while (retryCount < MAX_RETRIES) {
            try {
                // Проверяем, переданы ли координаты (формат: "lat,lon")
                val coordinates = city.split(",")
                val (latitude, longitude, cityName) = if (coordinates.size == 2 && 
                    coordinates[0].toDoubleOrNull() != null && 
                    coordinates[1].toDoubleOrNull() != null) {
                    // Если переданы координаты
                    val lat = coordinates[0].toDouble()
                    val lon = coordinates[1].toDouble()
                    Log.d("WeatherRepository", "Using coordinates directly: lat=$lat, lon=$lon")
                    // Используем Android Geocoder для определения города
                    val detectedCity = androidGeocoder.getCityFromCoordinates(lat, lon)
                    Log.d("WeatherRepository", "Detected city: $detectedCity")
                    Triple(lat, lon, detectedCity)
                } else {
                    // Если передано название города - получаем координаты через геокодинг
                    Log.d("WeatherRepository", "Searching for city: $city")
                    
                    // Сначала пробуем через Android Geocoder (работает офлайн)
                    val localCoordinates = androidGeocoder.getCoordinatesFromCity(city)
                    if (localCoordinates != null) {
                        Log.d("WeatherRepository", "Got coordinates from Android Geocoder: lat=${localCoordinates.first}, lon=${localCoordinates.second}")
                        Triple(localCoordinates.first, localCoordinates.second, city)
                    } else {
                        // Если не нашли локально, используем API геокодинга
                        val geocodingResult = geocodingApi.searchCity(name = city)
                        val location = geocodingResult.results?.firstOrNull()
                            ?: throw IOException("City not found: $city")
                        
                        Log.d("WeatherRepository", "Got coordinates from API for $city: lat=${location.latitude}, lon=${location.longitude}")
                        Triple(location.latitude ?: 0.0, location.longitude ?: 0.0, location.name ?: city)
                    }
                }
                
                // Получаем погоду по координатам
                val weatherDto = weatherApi.getWeather(
                    latitude = latitude,
                    longitude = longitude
                )
                
                // Преобразуем данные в доменную модель
                val (days, current) = weatherDto.toDomain(cityName)
                
                // Сохраняем в базу данных
                dao.insertWeather(current.toEntity())
                dao.deleteForecastDays(city)
                val forecastDayIds = dao.insertForecastDays(days.map { it.toForecastDayEntity() })
                days.forEachIndexed { index, day ->
                    val date = day.time
                    val forecastDayId = forecastDayIds[index]
                    val hours = day.hours.map { it.toEntity(forecastDayId, date) }
                    dao.insertHours(hours)
                }
                
                // Удаляем старые данные
                val twoHoursAgo = System.currentTimeMillis() - CACHE_DURATION
                dao.deleteOldWeather(twoHoursAgo)
                
                return@withContext Pair(days, current)
            } catch (e: Exception) {
                Log.e("WeatherRepository", "Error loading weather: ${e.message}", e)
                lastException = e
                retryCount++
                if (retryCount < MAX_RETRIES) {
                    kotlinx.coroutines.delay(RETRY_DELAY * retryCount)
                }
            }
        }
        
        // Если не удалось загрузить данные, пытаемся вернуть кэшированные
        val cachedWeather = dao.getWeather(city)
        val forecastDays = dao.getForecastDays(city)
        if (cachedWeather != null && forecastDays.isNotEmpty()) {
            val days = forecastDays.map { day ->
                val hours = dao.getHours(day.id).map { it.toDomain() }
                day.toDomain(hours)
            }
            val current = days.firstOrNull() ?: cachedWeather.toDomain(emptyList())
            return@withContext Pair(days, current)
        } else {
            throw lastException ?: IOException("Failed to load weather data after $MAX_RETRIES attempts")
        }
    }
}