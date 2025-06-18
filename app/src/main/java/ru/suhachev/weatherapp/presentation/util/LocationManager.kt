package ru.suhachev.weatherapp.presentation.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val context: Context
) {
    private val _locationState = MutableStateFlow<LocationState>(LocationState.Initial)
    val locationState: StateFlow<LocationState> = _locationState

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    _locationState.value = LocationState.LocationAvailable(location)
                } else {
                    _locationState.value = LocationState.Error("Location is null")
                }
                stopLocationUpdates()
            }
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()
        try {
            fusedLocationClient?.requestLocationUpdates(
                request,
                locationCallback!!,
                context.mainLooper
            )
        } catch (e: SecurityException) {
            _locationState.value = LocationState.PermissionDenied
        }
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }

    sealed interface LocationState {
        data object Initial : LocationState
        data class LocationAvailable(val location: Location) : LocationState
        data object PermissionDenied : LocationState
        data class Error(val message: String) : LocationState
    }
}