package ru.suhachev.weatherapp.util

import android.location.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor() {

    sealed class LocationState {
        object Initial : LocationState()
        data class LocationAvailable(val location: Location) : LocationState()
        object LocationDisabled : LocationState()
        object PermissionDenied : LocationState()
        data class Error(val message: String) : LocationState()
    }

}