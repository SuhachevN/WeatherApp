package ru.suhachev.weatherapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.suhachev.weatherapp.domain.model.WeatherModel
import androidx.compose.ui.text.font.FontFamily
import coil.compose.AsyncImage

@Composable
fun SimpleListItem(
    weather: WeatherModel,
    showRange: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = weather.time.takeIf { it.isNotBlank() } ?: "-",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = weather.condition.takeIf { it.isNotBlank() } ?: "-",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showRange) {
                        val maxTemp = weather.maxTemp.takeIf { it.isNotBlank() } ?: "-"
                        val minTemp = weather.minTemp.takeIf { it.isNotBlank() } ?: "-"
                        "${maxTemp}°C/${minTemp}°C"
                    } else {
                        weather.currentTemp.takeIf { it.isNotBlank() }?.let { "$it°C" } ?: "-"
                    },
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily.Monospace,
                    softWrap = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AsyncImage(
                model = weather.icon.takeIf { it.isNotBlank() } ?: "https://cdn.weatherapi.com/weather/64x64/day/302.png",
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
            )
        }
    }
}