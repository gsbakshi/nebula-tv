package com.example.nebula.home.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.example.nebula.home.WeatherState
import com.example.nebula.ui.theme.NebulaAmber
import com.example.nebula.ui.theme.NebulaGlow
import com.example.nebula.ui.theme.StarWhite

@Composable
fun WeatherWidget(weatherState: WeatherState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        when (weatherState) {
            WeatherState.Loading -> Text(
                text = "ACQUIRING WEATHER…",
                style = TextStyle(
                    fontSize = 9.sp,
                    letterSpacing = 4.sp,
                    color = StarWhite.copy(alpha = 0.22f),
                ),
            )
            is WeatherState.Success -> {
                // Temperature — precision instrument readout
                Text(
                    text = weatherState.temperature,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Thin,
                        fontSize = 52.sp,
                        letterSpacing = (-1).sp,
                        color = StarWhite,
                    ),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = weatherState.description.uppercase(),
                    style = TextStyle(
                        fontSize = 9.sp,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Normal,
                        color = NebulaGlow.copy(alpha = 0.65f),
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = weatherState.city.uppercase(),
                    style = TextStyle(
                        fontSize = 9.sp,
                        letterSpacing = 3.sp,
                        color = StarWhite.copy(alpha = 0.28f),
                    ),
                )
            }
            is WeatherState.Error -> Text(
                text = weatherState.message,
                style = TextStyle(
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    color = NebulaAmber.copy(alpha = 0.6f),
                ),
            )
        }
    }
}
