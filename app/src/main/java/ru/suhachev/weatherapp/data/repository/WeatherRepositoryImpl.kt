package ru.suhachev.weatherapp.data.repository

import ru.suhachev.weatherapp.data.local.WeatherDao
import ru.suhachev.weatherapp.data.mapper.*
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.domain.model.WeatherModel
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
    private val dao: WeatherDao,
    private val apiKey: String
) : WeatherRepository {
    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000L
        private const val MAX_RETRIES = 2
        private const val RETRY_DELAY = 500L
        private const val TAG = "WeatherRepository"
    }

    override suspend fun getWeather(city: String, forceRefresh: Boolean): Pair<List<WeatherModel>, WeatherModel> {
        val currentTime = System.currentTimeMillis()
        val cachedWeather = withContext(Dispatchers.IO) { dao.getWeather(city) }
        val forecastDays = withContext(Dispatchers.IO) { dao.getForecastDays(city) }

        if (!forceRefresh && cachedWeather != null && forecastDays.isNotEmpty() &&
            currentTime - cachedWeather.lastUpdated < CACHE_DURATION) {
            val days = forecastDays.map { day ->
                val hours = withContext(Dispatchers.IO) { dao.getHours(day.id) }.map { it.toDomain() }
                day.toDomain(hours)
            }
            val current = days.firstOrNull() ?: cachedWeather.toDomain(emptyList())
            GlobalScope.launch {
                try {
                    updateWeatherFromApi(city)
                } catch (_: Exception) {}
            }
            return Pair(days, current)
        }

        return updateWeatherFromApi(city)
    }

    private suspend fun updateWeatherFromApi(city: String): Pair<List<WeatherModel>, WeatherModel> {
        var retryCount = 0
        var lastException: Exception? = null
        while (retryCount < MAX_RETRIES) {
            try {
                val dto = api.getWeather(apiKey = apiKey, city = city)
                val (days, current) = dto.toDomain()
                withContext(Dispatchers.IO) { dao.insertWeather(current.toEntity()) }
                withContext(Dispatchers.IO) { dao.deleteForecastDays(city) }
                val forecastDayIds = withContext(Dispatchers.IO) { dao.insertForecastDays(days.map { it.toForecastDayEntity() }) }
                days.forEachIndexed { index, day ->
                    val date = day.time
                    val forecastDayId = forecastDayIds[index]
                    val hours = day.hours.map { it.toEntity(forecastDayId, date) }
                    withContext(Dispatchers.IO) { dao.insertHours(hours) }
                }
                val twoHoursAgo = System.currentTimeMillis() - CACHE_DURATION
                withContext(Dispatchers.IO) { dao.deleteOldWeather(twoHoursAgo) }
                return Pair(days, current)
            } catch (e: Exception) {
                lastException = e
                retryCount++
                if (retryCount < MAX_RETRIES) {
                    kotlinx.coroutines.delay(RETRY_DELAY * retryCount)
                }
            }
        }
        val cachedWeather = withContext(Dispatchers.IO) { dao.getWeather(city) }
        val forecastDays = withContext(Dispatchers.IO) { dao.getForecastDays(city) }
        if (cachedWeather != null && forecastDays.isNotEmpty()) {
            val days = forecastDays.map { day ->
                val hours = withContext(Dispatchers.IO) { dao.getHours(day.id) }.map { it.toDomain() }
                day.toDomain(hours)
            }
            val current = days.firstOrNull() ?: cachedWeather.toDomain(emptyList())
            return Pair(days, current)
        } else {
            throw lastException ?: IOException("Failed to load weather data after $MAX_RETRIES attempts")
        }
    }
} 