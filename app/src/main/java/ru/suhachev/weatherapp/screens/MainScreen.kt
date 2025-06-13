package ru.suhachev.weatherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.suhachev.weatherapp.presentation.viewmodel.WeatherViewModel
import ru.suhachev.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun MainScreen(
    onSearchClick: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (state.current == null) {
            viewModel.loadWeather("Tyumen", showCacheFirst = true)
        }
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
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
                            state.current?.let { viewModel.loadWeather(it.city, forceRefresh = true) }
                        },
                        isLoading = state.isLoading
                    )
                    TubLayout(
                        modifier = Modifier.weight(0.6f),
                        current = state.current,
                        dayList = state.days,
                        selectedDayIndex = state.selectedDayIndex,
                        onDaySelected = { index -> viewModel.updateSelectedDay(index) }
                    )
                }
            }
        }
    }
} 