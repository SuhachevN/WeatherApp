package ru.suhachev.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.suhachev.weatherapp.presentation.navigation.Navigation
import ru.suhachev.weatherapp.presentation.ui.theme.WeatherAppTheme
import ru.suhachev.weatherapp.presentation.viewmodel.WeatherViewModel
import ru.suhachev.weatherapp.presentation.util.LocationManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import android.Manifest

@OptIn(ExperimentalPermissionsApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: WeatherViewModel = hiltViewModel()
                    val locationState by viewModel.locationState.collectAsStateWithLifecycle()
                    val locationPermissionState = rememberPermissionState(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    LaunchedEffect(locationPermissionState.status.isGranted, locationState) {
                        Log.d("MainActivity", "LaunchedEffect triggered: permissionGranted=${locationPermissionState.status.isGranted}, locationState=$locationState")
                        
                        when {
                            !locationPermissionState.status.isGranted -> {
                                Log.d("MainActivity", "Permission not granted, requesting permission and loading default city")
                                if (!locationPermissionState.status.isGranted) {
                                    locationPermissionState.launchPermissionRequest()
                                }
                                viewModel.loadWeather("Tyumen")
                            }
                            locationState is LocationManager.LocationState.Initial -> {
                                Log.d("MainActivity", "Permission granted, requesting location")
                                viewModel.requestLocation()
                            }
                            locationState is LocationManager.LocationState.LocationAvailable -> {
                                val state = locationState as LocationManager.LocationState.LocationAvailable
                                val coordinates = "${state.location.latitude},${state.location.longitude}"
                                Log.d("MainActivity", "Location available: $coordinates")
                                viewModel.loadWeather(coordinates)
                            }
                            locationState is LocationManager.LocationState.Error -> {
                                Log.d("MainActivity", "Location error: ${(locationState as LocationManager.LocationState.Error).message}")
                                viewModel.loadWeather("Tyumen")
                            }
                        }
                    }

                    Navigation(navController, viewModel)
                }
            }
        }
    }
}