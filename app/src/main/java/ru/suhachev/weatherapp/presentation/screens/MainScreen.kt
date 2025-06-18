package ru.suhachev.weatherapp.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.suhachev.weatherapp.presentation.viewmodel.WeatherViewModel
import ru.suhachev.weatherapp.presentation.ui.components.MainCard
import ru.suhachev.weatherapp.presentation.ui.components.TubLayout
import ru.suhachev.weatherapp.presentation.ui.theme.WeatherAppTheme

@Composable
fun MainScreen(
    onSearchClick: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    Log.d("MainScreen", "State received: isLoading=${state.isLoading}, hasCurrent=${state.current != null}, daysCount=${state.days.size}")
    state.current?.let {
        Log.d("MainScreen", "Current weather: city=${it.city}, temp=${it.currentTemp}, condition=${it.condition}")
    }

    WeatherAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (state.error != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.error ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = { viewModel.retry() }
                                ) {
                                    Text("Повторить")
                                }
                            }
                        }
                    }
                    MainCard(
                        modifier = Modifier.weight(0.4f),
                        weather = state.current,
                        dayWeather = state.days.getOrNull(state.selectedDayIndex),
                        onSearchClick = onSearchClick,
                        onSync = {
                            if (state.lastQuery.isNotEmpty()) {
                                viewModel.loadWeather(state.lastQuery, forceRefresh = true)
                            }
                        },
                        isLoading = state.isLoading
                    )
                    TubLayout(
                        modifier = Modifier.weight(0.6f),
                        dayList = state.days,
                        selectedDayIndex = state.selectedDayIndex,
                        onDaySelected = { index -> viewModel.updateSelectedDay(index) }
                    )
                }
            }
        }
    }
} 