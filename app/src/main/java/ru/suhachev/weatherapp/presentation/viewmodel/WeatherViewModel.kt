package ru.suhachev.weatherapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.suhachev.weatherapp.data.model.CitySearchDto
import ru.suhachev.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.GetHourlyWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.SearchCityUseCase
import ru.suhachev.weatherapp.presentation.state.WeatherState
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyWeatherUseCase: GetHourlyWeatherUseCase,
    private val searchCityUseCase: SearchCityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CitySearchDto>>(emptyList())
    val searchResults: StateFlow<List<CitySearchDto>> = _searchResults.asStateFlow()

    private var isLoadingWeather = false

    fun loadWeather(city: String, forceRefresh: Boolean = false, showCacheFirst: Boolean = false) {
        if (isLoadingWeather) return
        isLoadingWeather = true
        val safeCity = if (city.isBlank() || city == "," || city == "0.0,0.0") "Tyumen" else city
        viewModelScope.launch {
            try {
                if (showCacheFirst || !forceRefresh) {
                    try {
                        val days = getHourlyWeatherUseCase(safeCity, false)
                        val current = getCurrentWeatherUseCase(safeCity, false)
                        if (days.isNotEmpty() && current != null) {
                            _state.value = WeatherState(
                                isLoading = true,
                                days = days,
                                current = current,
                                error = null,
                                selectedDayIndex = 0
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("WeatherViewModel", "Error loading cache", e)
                    }
                }

                if (forceRefresh || _state.value.current == null) {
                    _state.value = _state.value.copy(isLoading = true, error = null)
                    val days = getHourlyWeatherUseCase(safeCity, true)
                    val current = getCurrentWeatherUseCase(safeCity, true)
                    _state.value = WeatherState(
                        isLoading = false,
                        days = days,
                        current = current,
                        error = null,
                        selectedDayIndex = 0
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error loading weather", e)
                val message = when {
                    e is java.net.SocketTimeoutException -> "Не удалось получить данные: превышено время ожидания."
                    e is java.net.UnknownHostException -> "Нет подключения к интернету."
                    e is java.io.IOException -> "Ошибка сети. Проверьте подключение."
                    else -> e.message ?: "Неизвестная ошибка"
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = message
                )
            } finally {
                isLoadingWeather = false
            }
        }
    }

    fun searchCity(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val results = searchCityUseCase(query)
                _searchResults.value = results
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error searching city", e)
                _searchResults.value = emptyList()
            }
        }
    }

    fun updateSelectedDay(index: Int) {
        _state.value = _state.value.copy(selectedDayIndex = index)
    }

    fun retry() {
        state.value.current?.let { current ->
            loadWeather(current.city, true)
        }
    }
} 