package ru.suhachev.weatherapp.data.repository

import android.util.Log
import ru.suhachev.weatherapp.data.local.WeatherDao
import ru.suhachev.weatherapp.data.mapper.toDomain
import ru.suhachev.weatherapp.data.mapper.toEntity
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.domain.model.WeatherModel
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val dao: WeatherDao,
    private val apiKey: String
) : WeatherRepository {
    companion object {
        private const val CACHE_DURATION = 2 * 60 * 60 * 1000L
        private const val TAG = "WeatherRepository"
    }

    override suspend fun getWeather(city: String): Pair<List<WeatherModel>, WeatherModel> {
        val cachedWeather = dao.getWeather(city)
        val currentTime = System.currentTimeMillis()

        if (cachedWeather != null && currentTime - cachedWeather.lastUpdated < CACHE_DURATION) {
            Log.d(TAG, "Loading weather from cache for city: $city")
            return cachedWeather.toDomain()
        }

        Log.d(TAG, "Loading weather from API for city: $city")
        val dto = api.getWeather(apiKey = apiKey, city = city)
        val weather = dto.toDomain()

        Log.d(TAG, "Saving weather to cache for city: $city")
        dao.insertWeather(weather.second.toEntity())

        val twoHoursAgo = currentTime - CACHE_DURATION
        dao.deleteOldWeather(twoHoursAgo)
        
        return weather
    }
} 