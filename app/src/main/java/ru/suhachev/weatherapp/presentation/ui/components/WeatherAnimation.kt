package ru.suhachev.weatherapp.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

private fun mapConditionToAnimationFile(condition: String): String {
    return when {
        condition.contains("Ясно", ignoreCase = true) -> "clear.json"
        condition.contains("Переменная облачность", ignoreCase = true) -> "cloudy.json"
        condition.contains("Туман", ignoreCase = true) -> "mist.json"
        condition.contains("Морось", ignoreCase = true) -> "rainy.json"
        condition.contains("Дождь", ignoreCase = true) -> "rainy.json"
        condition.contains("Ливень", ignoreCase = true) -> "rainy.json"
        condition.contains("Снег", ignoreCase = true) -> "mist.json" // Используем mist для снега, так как нет snow.json
        condition.contains("Снегопад", ignoreCase = true) -> "mist.json"
        condition.contains("Гроза", ignoreCase = true) -> "thunder.json"
        else -> "sunny.json"
    }
}

@Composable
fun WeatherAnimation(
    modifier: Modifier = Modifier,
    weatherType: String
) {
    val animationFile = mapConditionToAnimationFile(weatherType)
    val composition = rememberLottieComposition(
        LottieCompositionSpec.Asset("weather_animations/$animationFile")
    )

    LottieAnimation(
        composition = composition.value,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
} 