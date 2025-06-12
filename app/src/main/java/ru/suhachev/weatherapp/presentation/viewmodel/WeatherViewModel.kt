package ru.suhachev.weatherapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.suhachev.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.GetHourlyWeatherUseCase
import ru.suhachev.weatherapp.presentation.state.WeatherState
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyWeatherUseCase: GetHourlyWeatherUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    fun loadWeather(city: String) {
        val safeCity = if (city.isBlank() || city == "," || city == "0.0,0.0") "Tyumen" else city
        Log.d("WeatherApp", "WeatherViewModel.loadWeather param: $safeCity (original: $city)")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val currentWeather = getCurrentWeatherUseCase(safeCity)
                val hourlyWeather = getHourlyWeatherUseCase(safeCity)
                _state.value = WeatherState(
                    isLoading = false,
                    days = hourlyWeather,
                    current = currentWeather,
                    error = null,
                    selectedDayIndex = 0
                )
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error loading weather", e)
                val message = when {
                    e is java.net.SocketTimeoutException -> "Не удалось получить данные: превышено время ожидания."
                    e is java.net.UnknownHostException -> "Нет подключения к интернету."
                    else -> e.message ?: "Неизвестная ошибка"
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = message
                )
            }
        }
    }

    fun updateSelectedDay(index: Int) {
        _state.value = _state.value.copy(selectedDayIndex = index)
    }

    fun retry() {
        state.value.current?.let { current ->
            loadWeather(current.city)
        }
    }
} 