package ru.suhachev.weatherapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

private fun mapConditionToAnimationFile(condition: String, icon: String): String {
    val isNight = icon.contains("night", ignoreCase = true)
    return when {
        isNight && (condition.contains("cloud", ignoreCase = true) || condition.equals("cloudy", ignoreCase = true) || condition.equals("partly cloudy", ignoreCase = true)) -> "night_cloudy.json"
        isNight && condition.equals("clear", ignoreCase = true) -> "clear_night.json"
        condition.equals("clear", ignoreCase = true) -> "clear.json"
        condition.equals("sunny", ignoreCase = true) -> "sunny.json"
        condition.contains("cloud", ignoreCase = true) || condition.equals("cloudy", ignoreCase = true) || condition.equals("partly cloudy", ignoreCase = true) -> "cloudy.json"
        condition.contains("rain", ignoreCase = true) && condition.contains("thunder", ignoreCase = true) -> "thunder.json"
        condition.contains("thunder", ignoreCase = true) -> "thunder.json"
        condition.contains("patchy light rain", ignoreCase = true) -> "rainy.json"
        condition.contains("rain", ignoreCase = true) || condition.contains("drizzle", ignoreCase = true) -> "rainy.json"
        condition.contains("mist", ignoreCase = true) -> "mist.json"
        else -> "sunny.json"
    }
}

@Composable
fun WeatherAnimation(
    modifier: Modifier = Modifier,
    weatherType: String,
    icon: String = "",
    size: Int = 100
) {
    val animationFile = mapConditionToAnimationFile(weatherType, icon)
    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset("weather_animations/$animationFile")
    )

    LottieAnimation(
        composition = composition.value,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
} 