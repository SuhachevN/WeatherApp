package ru.suhachev.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import ru.suhachev.weatherapp.presentation.viewmodel.WeatherViewModel
import ru.suhachev.weatherapp.screens.MainCard
import ru.suhachev.weatherapp.screens.TubLayout
import ru.suhachev.weatherapp.ui.theme.WeatherAppTheme
import androidx.activity.viewModels
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.activity.result.ActivityResultLauncher
import android.util.Log
import androidx.compose.foundation.background

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    getLastLocation { location ->
                        if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
                            val param = "${location.latitude},${location.longitude}"
                            Log.d("WeatherApp", "loadWeather param: $param")
                            viewModel.loadWeather(param)
                        } else {
                            Log.d("WeatherApp", "loadWeather param: Tyumen (no valid location)")
                            viewModel.loadWeather("Tyumen")
                        }
                    }
                } else {
                    Log.d("WeatherApp", "loadWeather param: Tyumen (no permission)")
                    viewModel.loadWeather("Tyumen")
                }
            }

        setContent {
            WeatherAppTheme {
                val state by viewModel.state.collectAsState()
                val scope = rememberCoroutineScope()

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
                                Text(
                                    text = state.error ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            MainCard(
                                modifier = Modifier.weight(0.4f),
                                weather = state.current,
                                dayWeather = state.days.getOrNull(state.selectedDayIndex),
                                onSync = {
                                    requestLocation()
                                }
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
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(onLocationReceived: (Location?) -> Unit) {
        val fineGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    onLocationReceived(location)
                }
                .addOnFailureListener {
                    onLocationReceived(null)
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestLocation() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}