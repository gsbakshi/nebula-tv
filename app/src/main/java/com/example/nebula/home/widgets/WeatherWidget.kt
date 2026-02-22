package com.example.nebula.home.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.nebula.home.WeatherState

@Composable
fun WeatherWidget(weatherState: WeatherState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        when (weatherState) {
            WeatherState.Loading -> Text(
                text = "Loading weather…",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.4f),
            )
            is WeatherState.Success -> {
                Text(
                    text = "${weatherState.temperature}  ${weatherState.description}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
                Text(
                    text = weatherState.city,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                )
            }
            is WeatherState.Error -> Text(
                text = weatherState.message,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFF6B6B).copy(alpha = 0.8f),
            )
        }
    }
}
