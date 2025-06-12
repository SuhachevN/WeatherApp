package ru.suhachev.weatherapp.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.suhachev.weatherapp.ui.components.WeatherAnimation
import kotlinx.coroutines.launch
import ru.suhachev.weatherapp.R
import ru.suhachev.weatherapp.domain.model.WeatherModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import ru.suhachev.weatherapp.util.WeatherConditionMapper

@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    weather: WeatherModel? = null,
    dayWeather: WeatherModel? = null,
    onSync: (() -> Unit)? = null
) {
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dayWeather?.time?.takeIf { it.isNotBlank() } ?: weather?.time?.takeIf { it.isNotBlank() } ?: "-",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentTime,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        WeatherAnimation(
                            weatherType = dayWeather?.condition ?: weather?.condition ?: "sunny",
                            icon = dayWeather?.icon ?: weather?.icon ?: "",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                
                Text(
                    text = dayWeather?.city?.takeIf { it.isNotBlank() } ?: weather?.city?.takeIf { it.isNotBlank() } ?: "-",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = dayWeather?.currentTemp?.takeIf { it.isNotBlank() }?.let { "$it°C" } 
                        ?: weather?.currentTemp?.takeIf { it.isNotBlank() }?.let { "$it°C" } ?: "-",
                    fontSize = 45.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = dayWeather?.condition?.takeIf { it.isNotBlank() }?.let { 
                        stringResource(WeatherConditionMapper.getConditionResource(it))
                    } ?: weather?.condition?.takeIf { it.isNotBlank() }?.let {
                        stringResource(WeatherConditionMapper.getConditionResource(it))
                    } ?: "-",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Text(
                        text = dayWeather?.let {
                            val maxTemp = it.maxTemp.takeIf { t -> t.isNotBlank() } ?: "-"
                            val minTemp = it.minTemp.takeIf { t -> t.isNotBlank() } ?: "-"
                            "${maxTemp}°C/${minTemp}°C"
                        } ?: "-",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(onClick = { onSync?.invoke() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TubLayout(
    modifier: Modifier = Modifier,
    current: WeatherModel?,
    dayList: List<WeatherModel>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit
) {
    val tabList = listOf(
        stringResource(R.string.tab_hours),
        stringResource(R.string.tab_days)
    )
    val pagerState = rememberPagerState(initialPage = 0) { tabList.size }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = text,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            when (index) {
                0 -> HoursWeatherContent(dayList.getOrNull(selectedDayIndex))
                1 -> DaysWeatherContent(dayList, selectedDayIndex, onDaySelected)
            }
        }
    }
}

@Composable
private fun HoursWeatherContent(selectedDay: WeatherModel?) {
    val hours = selectedDay?.hours ?: emptyList()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(hours) { hour ->
            ListItem(
                weather = WeatherModel(
                    city = "",
                    time = hour.time,
                    currentTemp = hour.temp,
                    condition = hour.condition,
                    icon = hour.icon,
                    maxTemp = "",
                    minTemp = "",
                    hours = emptyList()
                ),
                showRange = false
            )
        }
    }
}

@Composable
private fun DaysWeatherContent(
    dayList: List<WeatherModel>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit
) {
    if (dayList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_weather_data),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(dayList) { weather ->
            val index = dayList.indexOf(weather)
            ListItem(
                weather = weather,
                showRange = true,
                isSelected = index == selectedDayIndex,
                onClick = { onDaySelected(index) }
            )
        }
    }
}