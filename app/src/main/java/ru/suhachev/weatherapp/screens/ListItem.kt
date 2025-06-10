package ru.suhachev.weatherapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.suhachev.weatherapp.util.WeatherConditionMapper

@Composable
fun ListItem(
    weather: ru.suhachev.weatherapp.domain.model.WeatherModel,
    showRange: Boolean = false,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weather.time.takeIf { it.isNotBlank() } ?: "-",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier.size(35.dp),
                    model = weather.icon.takeIf { it.isNotBlank() } ?: "https://cdn.weatherapi.com/weather/64x64/day/302.png",
                    contentDescription = stringResource(WeatherConditionMapper.getConditionResource(weather.condition))
                )
                if (!showRange) {
                    Text(
                        text = stringResource(WeatherConditionMapper.getConditionResource(weather.condition)),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Text(
                text = weather.currentTemp.takeIf { it.isNotBlank() }?.let { "$it°C" } ?: if (!showRange) "-" else "",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showRange) {
                val max = weather.maxTemp.takeIf { it.isNotBlank() }
                val min = weather.minTemp.takeIf { it.isNotBlank() }
                if (max != null && min != null) {
                    Text(
                        text = "$max°C/$min°C",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
} 