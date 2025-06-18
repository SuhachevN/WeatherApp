package ru.suhachev.weatherapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.suhachev.weatherapp.domain.usecase.GetWeatherUseCase
import ru.suhachev.weatherapp.presentation.state.WeatherState
import ru.suhachev.weatherapp.presentation.util.LocationManager
import ru.suhachev.weatherapp.presentation.util.CityAutocompleteService
import ru.suhachev.weatherapp.domain.model.CityAutocompleteModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationManager: LocationManager,
    private val cityAutocompleteService: CityAutocompleteService
) : ViewModel() {
    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cityAutocompleteSuggestions = MutableStateFlow<List<CityAutocompleteModel>>(emptyList())
    val cityAutocompleteSuggestions: StateFlow<List<CityAutocompleteModel>> = _cityAutocompleteSuggestions.asStateFlow()

    private var isLoadingWeather = false

    val locationState: StateFlow<LocationManager.LocationState> = locationManager.locationState

    init {
        // Подписываемся на предложения автодополнения
        viewModelScope.launch {
            cityAutocompleteService.suggestionsFlow
                .collect { suggestions ->
                    _cityAutocompleteSuggestions.value = suggestions
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Запускаем автодополнение
        viewModelScope.launch {
            cityAutocompleteService.search(query)
        }
    }

    fun loadWeather(city: String, forceRefresh: Boolean = false) {
        if (isLoadingWeather) return
        isLoadingWeather = true
        
        val safeCity = if (city.isBlank() || city == "," || city == "0.0,0.0") "Tyumen" else city
        
        Log.d("WeatherViewModel", "Loading weather for city: $safeCity, forceRefresh: $forceRefresh")
        
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                val (days, current) = getWeatherUseCase(safeCity, forceRefresh)
                
                Log.d("WeatherViewModel", "Weather loaded successfully:")
                Log.d("WeatherViewModel", "Current: city=${current.city}, temp=${current.currentTemp}, condition=${current.condition}")
                Log.d("WeatherViewModel", "Days count: ${days.size}")
                days.forEachIndexed { index, day ->
                    Log.d("WeatherViewModel", "Day $index: date=${day.time}, max=${day.maxTemp}, min=${day.minTemp}")
                }
                
                _state.value = _state.value.copy(
                    isLoading = false,
                    days = days,
                    current = current,
                    error = null,
                    selectedDayIndex = if (_state.value.selectedDayIndex >= days.size) 0 else _state.value.selectedDayIndex,
                    lastQuery = safeCity // Сохраняем оригинальный запрос
                )
                
                Log.d("WeatherViewModel", "State updated: isLoading=${_state.value.isLoading}, hasCurrent=${_state.value.current != null}, daysCount=${_state.value.days.size}")
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error loading weather", e)
                val message = when (e) {
                    is java.net.SocketTimeoutException -> "Не удалось получить данные: превышено время ожидания."
                    is java.net.UnknownHostException -> "Нет подключения к интернету."
                    is java.io.IOException -> "Ошибка сети. Проверьте подключение."
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

    fun updateSelectedDay(index: Int) {
        _state.value = _state.value.copy(selectedDayIndex = index)
    }

    fun retry() {
        state.value.current?.let { current ->
            loadWeather(current.city, true)
        }
    }

    fun requestLocation() {
        locationManager.startLocationUpdates()
    }

    fun selectCityFromAutocomplete(city: CityAutocompleteModel) {
        val coordinates = "${city.latitude},${city.longitude}"
        loadWeather(coordinates)
    }
} 